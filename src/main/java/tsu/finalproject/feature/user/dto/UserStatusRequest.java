package tsu.finalproject.feature.user.dto;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

public record UserStatusRequest(
        @NonNull @NotNull(message = "Deactivated status is required")
        Boolean deactivated
) {
}