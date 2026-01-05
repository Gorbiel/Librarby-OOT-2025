package agh.oot.librarby.book.dto;

public record BookEditionResponse(
        Long id,
        String isbn,
        Long bookId,
        String bookTitle,
        Integer pageCount,
        Integer publicationYear,
        String publisherName,
        String language
) {
}
