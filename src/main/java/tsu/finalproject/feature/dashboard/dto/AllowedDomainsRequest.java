package tsu.finalproject.feature.dashboard.dto;

import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record AllowedDomainsRequest(
        @NonNull @NotEmpty(message = "At least one allowed domain must be specified")
        List<String> domains
) {
}