package tsu.finalproject.feature.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CourseSessionRequest(
        @NonNull @NotBlank(message = "Session name is required (e.g., Group A)")
        String name,

        @NonNull @NotNull(message = "Max capacity is required")
        @Min(value = 1, message = "Max capacity must be at least 1")
        Integer maxCapacity,

        @NonNull @NotNull(message = "Professor ID is required")
        Long professorId,

        @NonNull @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NonNull @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NonNull @NotNull(message = "End time is required")
        LocalTime endTime,

        @NonNull @NotBlank(message = "Room is required")
        String room
) {
}