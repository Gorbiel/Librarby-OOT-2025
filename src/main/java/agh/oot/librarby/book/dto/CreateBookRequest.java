package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateBookRequest(
        @NotBlank(message = "Title is required")
        String title,

        Set<Genre> genres,

        AgeRating ageRating,

        @NotEmpty(message = "At least one author ID is required")
        Set<Long> authorIds
) {
}
