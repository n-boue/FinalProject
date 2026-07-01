package tsu.finalproject.feature.feed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.feed.enums.PostType;
import tsu.finalproject.feature.feed.enums.PostVisibility;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
        @NonNull Long id,
        @NonNull PostType type,
        @NonNull String title,
        @NonNull String body,
        @NonNull Integer week,
        @NonNull PostVisibility visibility,
        @NonNull String authorName,
        @NonNull LocalDateTime createdAt,
        @NonNull List<AttachmentResponse> attachments,

        @Nullable LocalDateTime dueDate,
        @Nullable Integer maxPoints,
        @Nullable Integer timeLimitMinutes,
        @Nullable Integer passingScore
) {
}