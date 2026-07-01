package tsu.finalproject.feature.storage.event;

import org.jspecify.annotations.NonNull;

public record FileDeletionEvent(@NonNull String objectKey) {
}