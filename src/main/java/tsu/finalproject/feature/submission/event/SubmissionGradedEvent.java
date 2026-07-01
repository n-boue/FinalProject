package tsu.finalproject.feature.submission.event;

import org.jspecify.annotations.NonNull;

public record SubmissionGradedEvent(
        @NonNull Long studentId,
        @NonNull Long courseId
) {
}