package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Author data returned by the API")
public record AuthorResponse(
        @Schema(description = "Author ID", example = "5")
        Long id,

        @Schema(description = "Author first name", example = "F. Scott")
        String firstName,

        @Schema(description = "Author last name", example = "Fitzgerald")
        String lastName
) {}
