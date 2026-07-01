package tsu.finalproject.feature.directory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfessorDirectoryResponse(
        @NonNull Long id,
        @NonNull String firstName,
        @NonNull String lastName,
        @NonNull String email,
        @Nullable String department,
        @Nullable String title,
        @Nullable String office,
        @Nullable String profilePictureUrl
) {
}