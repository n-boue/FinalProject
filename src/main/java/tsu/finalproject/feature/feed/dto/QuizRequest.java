package tsu.finalproject.feature.feed.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import tsu.finalproject.feature.feed.enums.PostVisibility;

import java.time.LocalDateTime;
import java.util.List;

public record QuizRequest(
        @NonNull @NotBlank(message = "Title is required") String title,
        @NonNull @NotBlank(message = "Body is required") String body,
        @NonNull @NotNull(message = "Week is required") @Min(1) Integer week,
        @NonNull @NotNull(message = "Visibility is required") PostVisibility visibility,
        @NonNull List<String> attachmentKeys,

        @NonNull @NotNull(message = "Due date is required") LocalDateTime dueDate,
        @NonNull @NotNull(message = "Time limit is required") @Min(1) Integer timeLimitMinutes,
        @NonNull @NotNull(message = "Passing score is required") @Min(0) Integer passingScore,
        @NonNull @NotNull(message = "Shuffle preference is required") Boolean shuffleQuestions,

        @NonNull @NotEmpty(message = "A quiz must have at least one question")
        List<@Valid QuizQuestionRequest> questions
) {
}