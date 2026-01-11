package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Optional query parameters for filtering books")
public record BookQueryParams(
        @Schema(description = "Substring match for title", example = "hobbit")
        String title,

        @Schema(description = "Filter by author ID", example = "5")
        Long authorId,

        @Schema(description = "Filter by genre", example = "FANTASY")
        Genre genre,

        @Schema(description = "Filter by age rating", example = "EVERYONE")
        AgeRating ageRating
) {}
