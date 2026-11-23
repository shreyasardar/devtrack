
package devtrack_api.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String content;
    private Long issueId;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;

    public CommentResponse(
            Long id,
            String content,
            Long issueId,
            Long userId,
            String userName,
            LocalDateTime createdAt) {

        this.id = id;
        this.content = content;
        this.issueId = issueId;
        this.userId = userId;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
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
