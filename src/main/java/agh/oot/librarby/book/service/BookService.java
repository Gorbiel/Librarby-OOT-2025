package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.*;

import java.util.List;

public interface BookService {
    BookResponse createBook(BookCreateRequest request);

    BookResponse getBookById(Long bookId);

    MultipleBooksResponse getBooks(BookQueryParams params);

    BookResponse updateBook(Long bookId, BookUpdateRequest request);

    BookResponse addAuthor(Long bookId, Long authorId);

    BookResponse removeAuthor(Long bookId, Long authorId);

    BookResponse addGenre(Long bookId, String genre);

    BookResponse removeGenre(Long bookId, String genre);

    BookResponse setAgeRating(Long bookId, AgeRatingUpdateRequest request);

    void deleteBook(Long bookId);
}
