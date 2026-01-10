package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Book data returned by the API")
public record BookResponse(
        @Schema(description = "Book (title-level) ID", example = "10")
        Long id,

        @Schema(description = "Book title", example = "The Great Gatsby")
        String title,

        @Schema(description = "Genres assigned to the book", example = "[\"FICTION\",\"LITERARY_FICTION\"]")
        Set<Genre> genres,

        @Schema(description = "Age rating of the book", example = "ADULT", nullable = true)
        AgeRating ageRating,

        @Schema(description = "Authors assigned to the book")
        Set<AuthorResponse> authors
) {}
