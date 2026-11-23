
package devtrack_api.service;

import devtrack_api.dto.IssueRequest;
import devtrack_api.dto.IssueResponse;
import devtrack_api.dto.LabelResponse;

import devtrack_api.entity.Issue;
import devtrack_api.entity.Project;
import devtrack_api.entity.User;
import devtrack_api.entity.Label;

import devtrack_api.enums.IssueStatus;
import devtrack_api.enums.IssuePriority;
import devtrack_api.enums.UserRole;

import devtrack_api.exception.ResourceNotFoundException;

import devtrack_api.repository.IssueRepository;
import devtrack_api.repository.ProjectRepository;
import devtrack_api.repository.UserRepository;
import devtrack_api.repository.LabelRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            LabelRepository labelRepository,
            ActivityService activityService,
            NotificationService notificationService
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.activityService = activityService;
        this.notificationService = notificationService;
    }

    // CONVERT ISSUE ENTITY INTO RESPONSE DTO
    private IssueResponse toResponse(Issue issue) {

        List<LabelResponse> labels = issue.getLabels()
                .stream()
                .map(label -> new LabelResponse(
                        label.getId(),
                        label.getName(),
                        label.getDescription(),
                        label.getColor(),
                        label.getProject().getId()
                ))
                .toList();

        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getProject().getId(),
                issue.getAssignee() != null
                        ? issue.getAssignee().getId() : null,
                issue.getAssignee() != null
                        ? issue.getAssignee().getName() : null,
                issue.getCreatedAt(),
                labels
        );
    }

    // CREATE ISSUE + AUTOMATIC ACTIVITY LOG
    public IssueResponse createIssue(
            Long projectId,
            IssueRequest request
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with ID: " + projectId
                        )
                );

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setPriority(request.getPriority());
        issue.setProject(project);
        issue.setStatus(IssueStatus.OPEN);

        Issue savedIssue = issueRepository.save(issue);

        activityService.createActivity(
                savedIssue.getId(),
                "ISSUE_CREATED",
                "Issue created with priority " + savedIssue.getPriority()
        );

        return toResponse(savedIssue);
    }

    // GET SINGLE ISSUE
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(
            Long projectId,
            Long issueId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        return toResponse(issue);
    }

    // GET PROJECT ISSUES WITH FILTERING, SEARCH,
    // PAGINATION AND SORTING
    @Transactional(readOnly = true)
    public Page<IssueResponse> getProjectIssues(
            Long projectId,
            IssueStatus status,
            IssuePriority priority,
            String search,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with ID: " + projectId
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination values"
            );
        }

        Set<String> allowedSortFields = Set.of(
                "id", "title", "status", "priority", "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }

        Sort.Direction sortDirection =
                Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        String searchTerm = (search == null || search.isBlank())
                ? null : search.trim();

        Page<Issue> issues = issueRepository.searchAndFilterIssues(
                projectId,
                searchTerm,
                status,
                priority,
                pageable
        );

        return issues.map(this::toResponse);
    }

    // UPDATE ISSUE STATUS + AUTOMATIC ACTIVITY LOG
    public IssueResponse updateIssueStatus(
            Long projectId,
            Long issueId,
            IssueStatus newStatus
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        IssueStatus oldStatus = issue.getStatus();

        boolean validTransition =
                (oldStatus == IssueStatus.OPEN
                        && newStatus == IssueStatus.IN_PROGRESS)
                ||
                (oldStatus == IssueStatus.IN_PROGRESS
                        && newStatus == IssueStatus.DONE)
                ||
                (oldStatus == IssueStatus.DONE
                        && newStatus == IssueStatus.IN_PROGRESS);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                    + oldStatus + " to " + newStatus
            );
        }

        issue.setStatus(newStatus);

        Issue savedIssue = issueRepository.save(issue);

        activityService.createActivity(
                savedIssue.getId(),
                "STATUS_CHANGED",
                "Status changed from " + oldStatus + " to " + newStatus
        );

        return toResponse(savedIssue);
    }

    // UPDATE ISSUE DETAILS
   
// UPDATE ISSUE DETAILS + AUTOMATIC ACTIVITY LOG
public IssueResponse updateIssue(
        Long projectId,
        Long issueId,
        IssueRequest request
) {

    Issue issue = issueRepository.findById(issueId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Issue not found with ID: " + issueId
                    )
            );

    if (!issue.getProject().getId().equals(projectId)) {
        throw new ResourceNotFoundException(
                "Issue does not belong to this project"
        );
    }

    // Store old values before updating
    String oldTitle = issue.getTitle();
    String oldDescription = issue.getDescription();
    IssuePriority oldPriority = issue.getPriority();

    // Update issue fields
    issue.setTitle(request.getTitle());
    issue.setDescription(request.getDescription());
    issue.setPriority(request.getPriority());

    Issue savedIssue = issueRepository.save(issue);

    // Build activity details only for fields that changed
    StringBuilder details = new StringBuilder();

    if (!oldTitle.equals(savedIssue.getTitle())) {
        details.append("Title changed from '")
                .append(oldTitle)
                .append("' to '")
                .append(savedIssue.getTitle())
                .append("'. ");
    }

    if (!java.util.Objects.equals(
            oldDescription, savedIssue.getDescription())) {
        details.append("Description updated. ");
    }

    if (oldPriority != savedIssue.getPriority()) {
        details.append("Priority changed from ")
                .append(oldPriority)
                .append(" to ")
                .append(savedIssue.getPriority())
                .append(". ");
    }

    // Log only if something actually changed
    if (details.length() > 0) {
        activityService.createActivity(
                savedIssue.getId(),
                "ISSUE_UPDATED",
                details.toString().trim()
        );
    }

    return toResponse(savedIssue);
}


    // DELETE ISSUE
    public void deleteIssue(
            Long projectId,
            Long issueId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        issueRepository.delete(issue);
    }

    // ASSIGN ISSUE TO DEVELOPER + AUTOMATIC ACTIVITY LOG
    public IssueResponse assignIssue(
            Long projectId,
            Long issueId,
            Long userId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (user.getRole() != UserRole.DEVELOPER) {
            throw new IllegalArgumentException(
                    "Issues can only be assigned to developers"
            );
        }

        issue.setAssignee(user);

        Issue savedIssue = issueRepository.save(issue);

        activityService.createActivity(
                savedIssue.getId(),
                "ISSUE_ASSIGNED",
                "Issue assigned to " + user.getName()
        );
        notificationService.createNotification(
        user.getId(),
        "You have been assigned issue #" + savedIssue.getId()
                + ": " + savedIssue.getTitle()
);

        return toResponse(savedIssue);
        
    }

    // ASSIGN LABEL TO ISSUE
    
    // ASSIGN LABEL TO ISSUE + AUTOMATIC ACTIVITY LOG
    public IssueResponse assignLabel(
            Long projectId,
            Long issueId,
            Long labelId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        Label label = labelRepository
                .findByIdAndProjectId(labelId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Label not found in this project: " + labelId
                        )
                );

        // Add label only if it is not already assigned
        boolean added = issue.getLabels().add(label);

        Issue savedIssue = issueRepository.save(issue);

        // Log only when a new label is assigned
        if (added) {
            activityService.createActivity(
                    savedIssue.getId(),
                    "LABEL_ASSIGNED",
                    "Label '" + label.getName() + "' assigned to issue"
            );
        }

        return toResponse(savedIssue);
    }


    // REMOVE LABEL FROM ISSUE
   
    // REMOVE LABEL FROM ISSUE + AUTOMATIC ACTIVITY LOG
    public IssueResponse removeLabel(
            Long projectId,
            Long issueId,
            Long labelId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with ID: " + issueId
                        )
                );

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue does not belong to this project"
            );
        }

        Label label = labelRepository
                .findByIdAndProjectId(labelId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Label not found in this project: " + labelId
                        )
                );

        // Remove label
        if (!issue.getLabels().remove(label)) {
            throw new IllegalArgumentException(
                    "This label is not assigned to the issue"
            );
        }

        Issue savedIssue = issueRepository.save(issue);

        activityService.createActivity(
                savedIssue.getId(),
                "LABEL_REMOVED",
                "Label '" + label.getName() + "' removed from issue"
        );

        return toResponse(savedIssue);
    }


    // GET ISSUES BY LABEL
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByLabel(
            Long projectId,
            Long labelId
    ) {

        Label label = labelRepository
                .findByIdAndProjectId(labelId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Label not found in this project"
                        )
                );

        List<Issue> issues =
                issueRepository.findDistinctByProjectIdAndLabelsId(
                        projectId,
                        label.getId()
                );

        return issues.stream()
                .map(this::toResponse)
                .toList();
    }


}
