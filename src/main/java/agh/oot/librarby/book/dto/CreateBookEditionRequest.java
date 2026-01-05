package agh.oot.librarby.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookEditionRequest(
        @NotBlank(message = "ISBN is required")
        String isbn,

        @NotNull(message = "Book ID is required")
        Long bookId,

        Integer pageCount,

        Integer publicationYear,

        Long publisherId,

        @NotBlank(message = "Language is required")
        String language
) {
}
