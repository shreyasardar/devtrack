package devtrack_api.dto;

import java.time.LocalDateTime;

public class ActivityResponse {

    private Long id;
    private String action;
    private String details;
    private Long issueId;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;

    public ActivityResponse(
            Long id,
            String action,
            String details,
            Long issueId,
            Long userId,
            String userName,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.action = action;
        this.details = details;
        this.issueId = issueId;
        this.userId = userId;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}