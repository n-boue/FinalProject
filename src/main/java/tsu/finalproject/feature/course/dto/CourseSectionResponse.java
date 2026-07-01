package tsu.finalproject.feature.course.dto;

import org.jspecify.annotations.NonNull;
import tsu.finalproject.feature.course.enums.SessionType;

public record CourseSectionResponse(
        @NonNull Long id,
        @NonNull Long courseId,
        @NonNull SessionType type
) {
}