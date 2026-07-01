package tsu.finalproject.feature.enrollment.dto;

import org.jspecify.annotations.NonNull;
import tsu.finalproject.feature.enrollment.enums.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record EnrollmentResponse(
        @NonNull Long id,
        @NonNull Long courseId,
        @NonNull String courseTitle,
        @NonNull EnrollmentStatus status,
        @NonNull Set<Long> sessionIds,
        @NonNull LocalDateTime enrolledAt
) {
}