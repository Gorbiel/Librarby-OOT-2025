package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.ISBN;

import java.time.Year;
import java.util.Locale;

public record BookEditionResponse(
        Long id,
        ISBN isbn,
        BookBriefResponse book,
        Integer pageCount,
        Year publicationYear,
        PublisherResponse publisher,
        Locale language
) {

}
