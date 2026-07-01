package tsu.finalproject.feature.feed.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record CommentRequest(
        @NonNull @NotBlank(message = "Comment body is required")
        String body
) {
}