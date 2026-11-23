
package devtrack_api.service;

import devtrack_api.dto.CommentRequest;
import devtrack_api.dto.CommentResponse;
import devtrack_api.entity.Comment;
import devtrack_api.entity.Issue;
import devtrack_api.entity.User;
import devtrack_api.exception.ResourceNotFoundException;
import devtrack_api.repository.CommentRepository;
import devtrack_api.repository.IssueRepository;
import devtrack_api.repository.UserRepository;
import devtrack_api.enums.UserRole;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public CommentService(
            CommentRepository commentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            ActivityService activityService) {

        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private CommentResponse toResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getIssue().getId(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getCreatedAt()
        );
    }

    // CREATE COMMENT + ACTIVITY LOG
    public CommentResponse createComment(
            Long projectId,
            Long issueId,
            CommentRequest request) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found with id: " + issueId));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue not found in project: " + projectId);
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setIssue(issue);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        activityService.createActivity(
                issueId,
                "COMMENT_CREATED",
                user.getName() + " added a comment"
        );

        return toResponse(savedComment);
    }

    // GET ALL COMMENTS FOR AN ISSUE
    @Transactional(readOnly = true)
    public List<CommentResponse> getIssueComments(
            Long projectId,
            Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found with id: " + issueId));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Issue not found in project: " + projectId);
        }

        return commentRepository
                .findByIssueIdOrderByCreatedAtAsc(issueId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // CHECK WHETHER USER OWNS THE COMMENT OR IS ADMIN
    private void verifyCommentPermission(Comment comment) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email));

        boolean isOwner = comment.getUser().getId()
                .equals(currentUser.getId());

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    "You can only modify your own comments");
        }
    }

    // UPDATE COMMENT + ACTIVITY LOG
    public CommentResponse updateComment(
            Long projectId,
            Long issueId,
            Long commentId,
            CommentRequest request) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + commentId));

        if (!comment.getIssue().getId().equals(issueId)
                || !comment.getIssue().getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Comment not found in this issue");
        }

        verifyCommentPermission(comment);

        comment.setContent(request.getContent());

        Comment savedComment = commentRepository.save(comment);

        activityService.createActivity(
                issueId,
                "COMMENT_UPDATED",
                "Comment " + commentId + " was updated"
        );

        return toResponse(savedComment);
    }

    // DELETE COMMENT + ACTIVITY LOG
    public void deleteComment(
            Long projectId,
            Long issueId,
            Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + commentId));

        if (!comment.getIssue().getId().equals(issueId)
                || !comment.getIssue().getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Comment not found in this issue");
        }

        verifyCommentPermission(comment);

        commentRepository.delete(comment);

        activityService.createActivity(
                issueId,
                "COMMENT_DELETED",
                "Comment " + commentId + " was deleted"
        );
    }
}
