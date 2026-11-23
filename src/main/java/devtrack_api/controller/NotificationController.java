
package devtrack_api.controller;

import devtrack_api.dto.NotificationResponse;
import devtrack_api.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET ALL MY NOTIFICATIONS
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }

    // GET UNREAD NOTIFICATIONS
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        return ResponseEntity.ok(
                notificationService.getUnreadNotifications()
        );
    }

    // MARK NOTIFICATION AS READ
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId)
        );
    }

    // GET UNREAD NOTIFICATION COUNT
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {

        long count = notificationService.getUnreadCount();

        return ResponseEntity.ok(
                Map.of("unreadCount", count)
        );
    }
}
