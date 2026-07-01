package tsu.finalproject.feature.feed.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.feed.enums.QuestionType;

import java.util.List;

public record QuizQuestionRequest(
        @NonNull @NotNull(message = "Question type is required")
        QuestionType type,

        @NonNull @NotBlank(message = "Question text is required")
        String text,

        @NonNull @NotNull(message = "Points are required") @Min(0)
        Integer points,

        @Nullable List<String> options,
        @Nullable List<Integer> correctOptionIndices
) {
}