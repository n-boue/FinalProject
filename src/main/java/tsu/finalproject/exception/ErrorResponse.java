package tsu.finalproject.exception;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

public record ErrorResponse(
        @NonNull LocalDateTime timestamp,
        @NonNull Integer status,
        @NonNull String error,
        @NonNull String message
) {
}
