package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.ISBN;
import java.time.Year;
import java.util.Locale;

public record UpdateBookEditionRequest(
        ISBN isbn,
        Integer pageCount,
        Year publicationYear,
        Long publisherId,
        Locale language
) {

}
