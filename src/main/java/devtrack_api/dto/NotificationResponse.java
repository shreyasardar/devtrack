
package devtrack_api.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String message;
    private boolean isRead;
    private Long userId;
    private LocalDateTime createdAt;

    public NotificationResponse(
            Long id,
            String message,
            boolean isRead,
            Long userId,
            LocalDateTime createdAt) {

        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
