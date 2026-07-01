package tsu.finalproject.feature.user.dto;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tsu.finalproject.feature.user.enums.Role;

public record UserResponse(
        @NonNull Long id,
        @NonNull String firstName,
        @NonNull String lastName,
        @NonNull String email,
        @NonNull String universityId,
        @NonNull Role role,
        @Nullable String profilePictureUrl
) {
}