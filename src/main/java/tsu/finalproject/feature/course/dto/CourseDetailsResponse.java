package tsu.finalproject.feature.course.dto;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record CourseDetailsResponse(
        @NonNull Long id,
        @NonNull String title,
        @NonNull String faculty,
        @NonNull String description,
        @NonNull Integer credits,
        @NonNull Long semesterId,
        @NonNull String semesterName,
        @NonNull Long professorId,
        @Nullable String professorName,
        @NonNull Boolean deactivated,
        List<CourseSectionDetails> sections
) {
}
