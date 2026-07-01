package tsu.finalproject.feature.submission.dto;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record QuizAnswerRequest(
        @NonNull Long questionId,
        @Nullable String providedText,
        @Nullable List<Integer> selectedOptionIndices
) {
}