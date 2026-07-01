package tsu.finalproject.feature.submission.dto;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record QuizAnswerResponse(
        @NonNull Long id,
        @NonNull Long questionId,
        @Nullable String providedText,
        @NonNull List<Integer> selectedOptionIndices,
        @Nullable Integer awardedPoints
) {
}