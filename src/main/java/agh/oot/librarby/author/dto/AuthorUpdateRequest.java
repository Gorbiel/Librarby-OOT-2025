package agh.oot.librarby.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for partially updating an author")
public record AuthorUpdateRequest(

        @Schema(description = "Author first name (required)", example = "Francis")
        @NotBlank
        String firstName,

        @Schema(description = "Author middle name (optional)", example = "Scott", nullable = true)
        String middleName,

        @Schema(description = "Author last name (optional)", example = "Fitzgerald", nullable = true)
        String lastName
) {}
