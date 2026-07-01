package tsu.finalproject.feature.dashboard.dto;

import org.jspecify.annotations.NonNull;
import java.util.List;

public record ScheduleResponse(
        @NonNull List<ScheduleEventResponse> events
) {
}