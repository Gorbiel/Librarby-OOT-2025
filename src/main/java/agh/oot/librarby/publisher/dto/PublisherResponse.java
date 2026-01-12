package agh.oot.librarby.publisher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Author data returned by the API")
public record PublisherResponse(
        @Schema(description = "Publisher ID", example = "3")
        Long id,

        @Schema(description = "Publisher name", example = "Penguin Random House")
        String name
) {}
