package tsu.finalproject.feature.submission.dto;

import org.jspecify.annotations.NonNull;
import java.util.List;

public record AssignmentSubmissionRequest(
        @NonNull List<String> attachmentKeys
) {
}