package tsu.finalproject.feature.semester.dto;

import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

public record SemesterResponse(
        @NonNull Long id,
        @NonNull String name,
        @NonNull LocalDate startDate,
        @NonNull LocalDate endDate,
        @NonNull LocalDate enrollmentStartDate,
        @NonNull LocalDate enrollmentEndDate,
        boolean isActive
) {
}