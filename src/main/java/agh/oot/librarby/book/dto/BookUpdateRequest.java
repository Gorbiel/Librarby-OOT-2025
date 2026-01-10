package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Fields that can be updated for a book (partial update)")
public record BookUpdateRequest(

        @Schema(description = "New title (optional)", example = "The Hobbit: Illustrated Edition", nullable = true)
        String title,

        @Schema(description = "New age rating (optional)", example = "EVERYONE", nullable = true)
        AgeRating ageRating
) {}
