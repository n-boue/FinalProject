package tsu.finalproject.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record UpdateUserProfileRequest(
        @NonNull @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NonNull @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @Nullable String phone,
        @Nullable String address,
        @Nullable String faculty,
        @Nullable String program,
        @Nullable String yearOfStudy,
        @Nullable String department,
        @Nullable String office,
        @Nullable String title
) {
}