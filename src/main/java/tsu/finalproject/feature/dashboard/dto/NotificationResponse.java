package tsu.finalproject.feature.dashboard.dto;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

public record NotificationResponse(
        @NonNull Long id,
        @NonNull String message,
        @NonNull String targetUrl,
        @NonNull Boolean isRead,
        @NonNull LocalDateTime createdAt
) {
}