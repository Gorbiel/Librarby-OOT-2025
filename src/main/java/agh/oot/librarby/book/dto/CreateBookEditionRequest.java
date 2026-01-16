package agh.oot.librarby.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;

public record CreateBookEditionRequest(
        @NotNull(message = "bookId cannot be blank")
        Long bookId,

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
