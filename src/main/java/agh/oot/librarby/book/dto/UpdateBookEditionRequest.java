package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Locale;

public record UpdateBookEditionRequest(
        @Schema(description = "ISBN number of the book edition", example = "978-3-16-148410-0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String isbn,

        @Schema(description = "Number of pages in the book edition", example = "350", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer pageCount,

        @Schema(description = "Publication year of the book edition", example = "2020", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer publicationYear,

        @Schema(description = "ID of the publisher of the book edition", example = "7", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long publisherId,

        @Schema(description = "Language of the book edition expressed as IETF BCP 47 tag or ISO language code", example = "fr", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Locale language
) {}
