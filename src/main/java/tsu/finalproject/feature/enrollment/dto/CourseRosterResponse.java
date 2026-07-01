package tsu.finalproject.feature.enrollment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseRosterResponse(
        @NonNull Long id, // enrollment id
        @NonNull Long studentId,
        @NonNull String studentFirstName,
        @NonNull String studentLastName,
        @NonNull String studentEmail,
        @NonNull LocalDateTime enrolledAt,
        @Nullable BigDecimal finalScore,
        @Nullable String finalGrade
) {
}