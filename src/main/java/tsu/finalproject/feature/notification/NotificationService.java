package tsu.finalproject.feature.notification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.dashboard.dto.NotificationResponse;
import tsu.finalproject.feature.notification.entity.Notification;
import tsu.finalproject.feature.notification.event.NotificationEvent;
import tsu.finalproject.feature.notification.repository.NotificationRepository;
import tsu.finalproject.feature.user.entity.User;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DomainLookupService domainLookupService;
    private final Clock clock;

    @Async
    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        User targetUser = domainLookupService.getUser(event.targetUserId());

        Notification notification = Notification.builder()
                                            .user(targetUser)
                                            .message(event.message())
                                            .targetUrl(event.targetUrl())
                                            .isRead(false)
                                            .createdAt(LocalDateTime.now(clock))
                                            .build();

        notificationRepository.save(notification);

        // Stretch goal: Push via WebSocket/SSE here (a bit complicated to implement, should focus on finishing the project for now)
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<NotificationResponse> getUserNotifications(@NonNull Long userId, Boolean unreadOnly, @NonNull Pageable pageable) {
        Page<Notification> notifications;

        if (Boolean.TRUE.equals(unreadOnly)) {
            notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return notifications.map(this::toResponse);
    }

    @Transactional
    @NonNull
    public NotificationResponse markAsRead(@NonNull Long userId, @NonNull Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                                            .orElseThrow(() -> new EntityNotFoundException("Notification not found."));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You do not own this notification.");
        }

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(@NonNull Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getTargetUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}