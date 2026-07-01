package tsu.finalproject.feature.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

public record CourseRequest(
        @NonNull @NotBlank(message = "Course title is required")
        String title,

        @NonNull @NotBlank(message = "Faculty is required")
        String faculty,

        @NonNull @NotBlank(message = "Course description is required")
        String description,

        @NonNull @NotNull(message = "Credits are required")
        @Min(value = 0, message = "Credits cannot be negative")
        Integer credits,

        @NonNull @NotNull(message = "Semester ID is required")
        Long semesterId,

        @NonNull @NotNull(message = "Professor ID is required")
        Long professorId
) {
}