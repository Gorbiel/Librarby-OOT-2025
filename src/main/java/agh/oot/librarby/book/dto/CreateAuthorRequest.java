package agh.oot.librarby.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {
}
