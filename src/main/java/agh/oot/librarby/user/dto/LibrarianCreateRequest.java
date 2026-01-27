package agh.oot.librarby.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LibrarianCreateRequest(
        @Schema(description = "Username for the new librarian", example = "librarian_jane", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String username
) {
}
