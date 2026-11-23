
package devtrack_api.service;

import devtrack_api.dto.NotificationResponse;
import devtrack_api.entity.Notification;
import devtrack_api.entity.User;
import devtrack_api.exception.ResourceNotFoundException;
import devtrack_api.repository.NotificationRepository;
import devtrack_api.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // GET CURRENTLY LOGGED-IN USER
    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email
                        )
                );
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getUser().getId(),
                notification.getCreatedAt()
        );
    }

    // CREATE NOTIFICATION FOR A USER
    public Notification createNotification(
            Long userId,
            String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);

        return notificationRepository.save(notification);
    }

    // GET ALL NOTIFICATIONS FOR CURRENT USER
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET UNREAD NOTIFICATIONS
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // MARK NOTIFICATION AS READ
    public NotificationResponse markAsRead(Long notificationId) {

        User user = getCurrentUser();

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with ID: "
                                        + notificationId
                        )
                );

        // Users can only mark their own notifications as read
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You cannot modify another user's notification"
            );
        }

        notification.setRead(true);

        return toResponse(notificationRepository.save(notification));
    }

    // COUNT UNREAD NOTIFICATIONS
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        User user = getCurrentUser();

        return notificationRepository
                .countByUserIdAndIsReadFalse(user.getId());
    }
}
