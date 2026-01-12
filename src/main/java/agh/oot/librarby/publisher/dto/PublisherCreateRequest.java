package agh.oot.librarby.publisher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a publisher")
public record PublisherCreateRequest(
        @Schema(description = "Publisher name (required)", example = "Penguin Random House")
        @NotBlank(message = "Publisher name is required")
        String name
) {
}
