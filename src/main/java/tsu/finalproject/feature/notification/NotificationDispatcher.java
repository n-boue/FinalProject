package tsu.finalproject.feature.notification;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tsu.finalproject.feature.notification.event.NotificationEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final ApplicationEventPublisher eventPublisher;

    public void dispatchCourseNotification(@NonNull Long targetUserId, @NonNull String courseTitle, @NonNull String action, @NonNull String targetUrl) {
        String message = String.format("Course '%s': %s", courseTitle, action);
        eventPublisher.publishEvent(new NotificationEvent(targetUserId, message, targetUrl));
    }

    public void dispatchSystemNotification(@NonNull Long targetUserId, @NonNull String message, @NonNull String targetUrl) {
        eventPublisher.publishEvent(new NotificationEvent(targetUserId, message, targetUrl));
    }

    public void dispatchBulkCourseNotification(@NonNull List<Long> targetUserIds, @NonNull String courseTitle, @NonNull String action, @NonNull String targetUrl) {
        String message = String.format("Course '%s': %s", courseTitle, action);
        targetUserIds.forEach(id -> eventPublisher.publishEvent(new NotificationEvent(id, message, targetUrl)));
    }
}