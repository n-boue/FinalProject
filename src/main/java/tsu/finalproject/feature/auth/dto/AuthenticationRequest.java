package tsu.finalproject.feature.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record AuthenticationRequest(
        @NonNull @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NonNull @NotBlank(message = "Password is required")
        String password
) {
}