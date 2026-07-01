package tsu.finalproject.feature.course.dto;

import org.jspecify.annotations.NonNull;
import tsu.finalproject.feature.course.enums.SessionType;

import java.util.List;

public record CourseSectionDetails(
        @NonNull Long id,
        @NonNull SessionType type,
        @NonNull List<CourseSessionResponse> sessions
) {
}