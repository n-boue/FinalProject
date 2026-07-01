package tsu.finalproject.feature.auth.dto;

import org.jspecify.annotations.NonNull;

public record AuthenticationResponse(
        @NonNull String token,
        @NonNull String refreshToken
) {
}
