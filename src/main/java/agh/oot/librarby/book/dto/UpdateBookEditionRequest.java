package agh.oot.librarby.book.dto;

import java.util.Locale;

public record UpdateBookEditionRequest(
        String isbn,
        Integer pageCount,
        Integer publicationYear,
        Long publisherId,
        Locale language
) {

}
