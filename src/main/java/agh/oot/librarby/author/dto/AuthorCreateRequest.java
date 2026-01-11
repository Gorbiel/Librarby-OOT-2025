package agh.oot.librarby.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating an author")
public record AuthorCreateRequest(

        @Schema(description = "Author first name (required). Can also be used as a pseudonym", example = "F.")
        @NotBlank
        String firstName,

        @Schema(description = "Author middle name (optional)", example = "Scott", nullable = true)
        String middleName,

        @Schema(description = "Author last name (optional)", example = "Fitzgerald", nullable = true)
        String lastName
) {}
