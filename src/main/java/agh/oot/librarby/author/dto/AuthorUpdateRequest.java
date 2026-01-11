package agh.oot.librarby.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for partially updating an author")
public record AuthorUpdateRequest(

        @Schema(description = "Author first name (optional in PATCH, but if provided must not be blank)",
                example = "Francis")
        String firstName,

        @Schema(description = "Author middle name (optional)", example = "Scott", nullable = true)
        String middleName,

        @Schema(description = "Author last name (optional)", example = "Fitzgerald", nullable = true)
        String lastName
) {}
