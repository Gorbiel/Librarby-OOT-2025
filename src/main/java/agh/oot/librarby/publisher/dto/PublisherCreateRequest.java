package agh.oot.librarby.publisher.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherCreateRequest(
        @NotBlank(message = "Publisher name is required")
        String name
) {
}
