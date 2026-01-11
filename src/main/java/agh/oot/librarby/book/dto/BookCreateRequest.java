package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Schema(description = "Request payload for creating a new book")
public record BookCreateRequest(

        @NotBlank
        @Schema(description = "Book title", example = "The Hobbit", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "Genres to assign to the book", example = "[\"FANTASY\",\"ACTION_AND_ADVENTURE\"]")
        Set<Genre> genres,

        @Schema(description = "Optional age rating", example = "EVERYONE", nullable = true)
        AgeRating ageRating,

        @Schema(description = "Existing author IDs to link to this book", example = "[5, 8]")
        Set<Long> authorIds
) {}
