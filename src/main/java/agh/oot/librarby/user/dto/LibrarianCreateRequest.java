package agh.oot.librarby.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LibrarianCreateRequest(
        @NotBlank
        String username
) {
}
