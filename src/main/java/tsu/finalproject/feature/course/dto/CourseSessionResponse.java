package tsu.finalproject.feature.course.dto;

import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CourseSessionResponse(
        @NonNull Long id,
        @NonNull Long sectionId,
        @NonNull String name,
        @NonNull Integer maxCapacity,
        @NonNull Long professorId,
        @NonNull String professorName,
        @NonNull DayOfWeek dayOfWeek,
        @NonNull LocalTime startTime,
        @NonNull LocalTime endTime,
        @NonNull String room
) {
}