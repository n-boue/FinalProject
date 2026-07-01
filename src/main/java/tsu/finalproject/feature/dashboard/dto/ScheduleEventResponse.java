package tsu.finalproject.feature.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.dashboard.enums.ScheduleEventType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScheduleEventResponse(
        @NonNull Long courseId,
        @NonNull String courseTitle,
        @NonNull String eventTitle,
        @NonNull ScheduleEventType eventType,
        @Nullable DayOfWeek dayOfWeek,
        @Nullable LocalTime startTime,
        @Nullable LocalTime endTime,
        @Nullable String room,
        @Nullable LocalDateTime exactDateTime
) {
}