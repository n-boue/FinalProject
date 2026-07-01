package tsu.finalproject.feature.submission.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubmissionResponse(
        @NonNull Long id,
        @NonNull Long studentId,
        @NonNull Long postId,
        @Nullable BigDecimal score,
        @Nullable String professorFeedback,
        @NonNull LocalDateTime submittedAt,
        @Nullable LocalDateTime gradedAt,
        @Nullable List<AttachmentResponse> attachments,
        @Nullable List<QuizAnswerResponse> quizAnswers
) {
}