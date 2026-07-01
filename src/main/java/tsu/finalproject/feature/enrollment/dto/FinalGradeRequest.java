package tsu.finalproject.feature.enrollment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public record FinalGradeRequest(
        @NonNull @NotNull(message = "Final score is required")
        @DecimalMin(value = "0.0", message = "Score cannot be negative")
        BigDecimal finalScore,

        @NonNull @NotBlank(message = "Final letter grade is required")
        String finalGrade
) {
}