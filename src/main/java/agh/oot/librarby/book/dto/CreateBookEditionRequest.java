package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;

public record CreateBookEditionRequest(
        @Schema(description = "ID of the book to which this edition belongs", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "bookId cannot be blank")
        Long bookId,

        @Schema(description = "ISBN of the book edition", example = "978-3-16-148410-0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ISBN cannot be blank")
        String isbn,

        @Schema(description = "Number of pages in the book edition", example = "320", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Page count is required")
        @Positive(message = "Page count must be positive")
        Integer pageCount,

        @Schema(description = "Publication year of the book edition", example = "2013", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Publication year is required")
        Integer publicationYear,

        @Schema(description = "ID of the publisher of the book edition", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Publisher ID is required")
        Long publisherId,

        @Schema(description = "Language of the book edition expressed as IETF BCP 47 tag or ISO language code", example = "en", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Language tag is required")
        String language
) {
}
