package tsu.finalproject.feature.enrollment.dto;

import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public record EnrollmentRequest(
        @NonNull @NotEmpty(message = "You must select at least one session to enroll.")
        Set<Long> sessionIds
) {
}