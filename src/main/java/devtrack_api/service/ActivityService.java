package devtrack_api.service;

import devtrack_api.entity.Activity;
import devtrack_api.entity.Issue;
import devtrack_api.entity.User;
import devtrack_api.repository.ActivityRepository;
import devtrack_api.repository.IssueRepository;
import devtrack_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import devtrack_api.dto.ActivityResponse;

import java.util.List;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            IssueRepository issueRepository,
            UserRepository userRepository
    ) {
        this.activityRepository = activityRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    // Convert current activity into a response
    public Activity createActivity(
            Long issueId,
            String action,
            String details
    ) {
        // Find the issue
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found")
                );

        // Identify the logged-in user using JWT authentication
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Create activity
        Activity activity = new Activity();
        activity.setIssue(issue);
        activity.setUser(user);
        activity.setAction(action);
        activity.setDetails(details);

        return activityRepository.save(activity);
    }

    // Get activity history for an issue
@Transactional(readOnly = true)
public List<ActivityResponse> getIssueActivities(Long issueId) {

    if (!issueRepository.existsById(issueId)) {
        throw new RuntimeException("Issue not found");
    }

    return activityRepository
            .findByIssueIdOrderByCreatedAtDesc(issueId)
            .stream()
            .map(this::toResponse)
            .toList();
}
    private ActivityResponse toResponse(Activity activity) {
    return new ActivityResponse(
            activity.getId(),
            activity.getAction(),
            activity.getDetails(),
            activity.getIssue().getId(),
            activity.getUser().getId(),
            activity.getUser().getName(),
            activity.getCreatedAt()
    );
}
}