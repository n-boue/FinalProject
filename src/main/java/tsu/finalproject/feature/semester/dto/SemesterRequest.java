package tsu.finalproject.feature.semester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

public record SemesterRequest(
        @NonNull @NotBlank(message = "Semester name is required")
        String name,

        @NonNull @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NonNull @NotNull(message = "End date is required")
        LocalDate endDate,

        @NonNull @NotNull(message = "Enrollment start date is required")
        LocalDate enrollmentStartDate,

        @NonNull @NotNull(message = "Enrollment end date is required")
        LocalDate enrollmentEndDate
) {
}