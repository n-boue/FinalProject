package tsu.finalproject.feature.course.dto;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import tsu.finalproject.feature.course.enums.SessionType;

public record CourseSectionRequest(
        @NonNull @NotNull(message = "Session type is required")
        SessionType type
) {
}