package tsu.finalproject.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record LogoutRequest(
        @NonNull @NotBlank(message = "Refresh token is required for revocation")
        String refreshToken
) {}