package tsu.finalproject.feature.submission.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public record GradeSubmissionRequest(
        @NonNull @NotNull(message = "Score is required")
        @DecimalMin(value = "0.0", message = "Score cannot be negative")
        BigDecimal score,

        @Nullable String professorFeedback
) {
}
