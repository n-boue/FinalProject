package tsu.finalproject.feature.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GradeRecordResponse(
        @NonNull Long courseId,
        @NonNull String courseTitle,
        @NonNull Long semesterId,
        @NonNull String semesterName,
        @Nullable BigDecimal finalScore,
        @Nullable String finalGrade
) {
}