package tsu.finalproject.feature.feed.dto;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.feed.enums.QuestionType;

import java.util.List;

public record QuizQuestionResponse(
        @NonNull Long id,
        @NonNull QuestionType type,
        @NonNull String text,
        @NonNull Integer points,
        @Nullable List<String> options,
        @Nullable List<Integer> correctOptionIndices
) {
}