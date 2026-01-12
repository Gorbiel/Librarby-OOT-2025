package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.ISBN;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Year;
import java.util.Locale;

@Schema(description = "Book edition data returned by the API")
public record BookEditionResponse(
        @Schema(description = "Book edition ID", example = "10")
        Long id,

        @Schema(description = "International Standard Book Number (ISBN) of the edition",
                example = "978-3-16-148410-0")
        ISBN isbn,

        @Schema(description = "Reference to the parent book (title-level)", implementation = BookBriefResponse.class)
        BookBriefResponse book,

        @Schema(description = "Number of pages in the edition", example = "320")
        Integer pageCount,

        @Schema(description = "Year of publication", example = "2013", type = "integer", format = "int32")
        Year publicationYear,

        @Schema(description = "Publisher information for this edition", implementation = PublisherResponse.class)
        PublisherResponse publisher,

        @Schema(description = "Language of the edition expressed as IETF BCP 47 tag or ISO language code",
                example = "en")
        Locale language
) {

}
