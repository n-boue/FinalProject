package tsu.finalproject.feature.notification.event;

import org.jspecify.annotations.NonNull;

public record NotificationEvent(
        @NonNull Long targetUserId,
        @NonNull String message,
        @NonNull String targetUrl
) {
}