package agh.oot.librarby.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBookEditionRequest(
        @NotBlank(message = "ISBN cannot be blank")
        String isbn,

        @NotNull(message = "Page count is required")
        @Positive(message = "Page count must be positive")
        Integer pageCount,

        @NotNull(message = "Publication year is required")
        Integer publicationYear,

        @NotNull(message = "Publisher ID is required")
        Long publisherId,

        @NotBlank(message = "Language tag is required")
        String language
) {
}
