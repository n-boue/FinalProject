package tsu.finalproject.feature.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

public record RegisterRequest(
        @NonNull @NotBlank(message = "First name is required")
        String firstName,

        @NonNull @NotBlank(message = "Last name is required")
        String lastName,

        @NonNull @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NonNull @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NonNull @NotBlank(message = "University ID is required")
        String universityId
) {
}