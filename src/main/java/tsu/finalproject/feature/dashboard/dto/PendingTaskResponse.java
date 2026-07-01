package tsu.finalproject.feature.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.feed.enums.PostType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PendingTaskResponse(
        @NonNull Long courseId,
        @NonNull String courseTitle,
        @NonNull Long postId,
        @NonNull String taskTitle,
        @NonNull PostType type,
        @Nullable LocalDateTime dueDate,
        @NonNull Integer maxPointsOrPassingScore
) {
}