package agh.oot.librarby.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Author data returned by the API")
public record AuthorResponse(
        @Schema(description = "Author ID", example = "5")
        Long id,

        @Schema(description = "Author first name", example = "F.")
        String firstName,

        @Schema(description = "Author middle name", example = "Scott")
        String middleName,

        @Schema(description = "Author last name", example = "Fitzgerald")
        String lastName
) {}
