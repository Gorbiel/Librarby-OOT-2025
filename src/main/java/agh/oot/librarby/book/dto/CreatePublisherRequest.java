package agh.oot.librarby.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePublisherRequest(
        @NotBlank(message = "Publisher name is required")
        String name
) {
}
