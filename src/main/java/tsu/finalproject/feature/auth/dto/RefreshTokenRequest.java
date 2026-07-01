package tsu.finalproject.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record RefreshTokenRequest(
        @NonNull @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}