package tsu.finalproject.feature.feed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentResponse(
        @NonNull Long id,
        @NonNull String body,
        @NonNull Long authorId,
        @NonNull String authorName,
        @Nullable String authorProfilePictureUrl,
        @NonNull LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt
) {
}