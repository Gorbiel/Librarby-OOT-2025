package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;

import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        Set<Genre> genres,
        AgeRating ageRating,
        Set<AuthorResponse> authors
) {
}
