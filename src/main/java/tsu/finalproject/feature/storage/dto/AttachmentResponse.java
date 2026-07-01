package tsu.finalproject.feature.storage.dto;

import org.jspecify.annotations.NonNull;

public record AttachmentResponse(
        @NonNull Long id,
        @NonNull String originalFileName,
        @NonNull String url,
        @NonNull Long sizeBytes
) {
}