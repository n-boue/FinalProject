package tsu.finalproject.feature.submission.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record QuizSubmissionRequest(
        @NonNull @NotEmpty(message = "Quiz submission must contain answers.")
        List<@Valid QuizAnswerRequest> answers
) {
}