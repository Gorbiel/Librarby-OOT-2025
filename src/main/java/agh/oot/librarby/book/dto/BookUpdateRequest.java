package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Fields that can be updated for a book (partial update)")
public record BookUpdateRequest(

        @Schema(description = "New title (optional)", example = "The Hobbit: Illustrated Edition", nullable = true)
        String title,

        @Schema(description = "New age rating (optional)", example = "EVERYONE", nullable = true)
        AgeRating ageRating,

        @Schema(description = "New list of genres (optional)", example = "[\"FANTASY\",\"ADVENTURE\"]", nullable = true)
        Set<Genre> genres,

        @Schema(description = "New list of authors (optional)", example = "[1, 2, 3]", nullable = true)
        Set<Long> authorIds
) {}
