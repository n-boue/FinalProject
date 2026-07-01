package tsu.finalproject.feature.dashboard.dto;

import org.jspecify.annotations.NonNull;

public record SystemStatsResponse(
        @NonNull Long totalUsers,
        @NonNull Long totalCourses,
        @NonNull Long activeSemestersCount
) {
}