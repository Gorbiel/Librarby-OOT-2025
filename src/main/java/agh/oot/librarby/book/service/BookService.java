package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.model.Genre;

/**
 * Service API for managing books.
 *
 * <p>Implementations perform validation and translate domain errors into appropriate HTTP
 * responses (for example by throwing {@code org.springframework.web.server.ResponseStatusException}).
 *
 * <p>All methods operate on DTOs defined in the {@code agh.oot.librarby.book.dto} package and
 * on the {@code Genre} enum where appropriate.
 *
 * @since 1.0
 */
public interface BookService {

    /**
     * Create a new book.
     *
     * @param request DTO containing data required to create a book. Must not be {@code null}.
     * @return the created book representation (including generated id).
     * @throws org.springframework.web.server.ResponseStatusException if the request is invalid
     *         (for example {@code 400}) or referenced entities (authors) are not found ({@code 404}).
     */
    BookResponse createBook(BookCreateRequest request);

    /**
     * Fetch a single book by its identifier.
     *
     * @param bookId identifier of the book to fetch; must not be {@code null}.
     * @return the book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404}).
     */
    BookResponse getBookById(Long bookId);

    /**
     * Query books using optional filters.
     *
     * <p>When {@code params} is {@code null} no filtering is applied. Individual fields inside
     * {@code BookQueryParams} may be {@code null} to indicate that the corresponding filter
     * should be skipped.
     *
     * @param params filter and paging parameters (may be {@code null}).
     * @return a wrapper containing zero or more book representations.
     */
    MultipleBooksResponse getBooks(BookQueryParams params);

    /**
     * Partially update a book.
     *
     * <p>Only non-{@code null} fields from {@code request} should be applied.
     *
     * @param bookId identifier of the book to update.
     * @param request partial update DTO.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or the request is invalid ({@code 400}).
     */
    BookResponse updateBook(Long bookId, BookUpdateRequest request);

    /**
     * Add an author to the book.
     *
     * <p>Operation is strict: adding an already linked author results in {@code 409 CONFLICT}.
     *
     * @param bookId identifier of the book.
     * @param authorId identifier of the author to link.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book or author is not found ({@code 404})
     *         or the author is already assigned to the book ({@code 409}).
     */
    BookResponse addAuthor(Long bookId, Long authorId);

    /**
     * Remove an author from the book.
     *
     * <p>Operation is strict: removing a non-assigned author results in {@code 409 CONFLICT}.
     *
     * @param bookId identifier of the book.
     * @param authorId identifier of the author to remove.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or the author is not assigned to the book ({@code 409}).
     */
    BookResponse removeAuthor(Long bookId, Long authorId);

    /**
     * Add a genre to the book.
     *
     * <p>Operation is strict: adding an already assigned genre results in {@code 409 CONFLICT}.
     *
     * @param bookId identifier of the book.
     * @param genre genre to add.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or the genre is already assigned to the book ({@code 409}).
     */
    BookResponse addGenre(Long bookId, Genre genre);

    /**
     * Remove a genre from the book.
     *
     * <p>Operation is strict: removing a non-assigned genre results in {@code 409 CONFLICT}.
     *
     * @param bookId identifier of the book.
     * @param genre genre to remove.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or the genre is not assigned to the book ({@code 409}).
     */
    BookResponse removeGenre(Long bookId, Genre genre);

    /**
     * Set the age rating for a book.
     *
     * @param bookId identifier of the book.
     * @param request DTO with the desired age rating; must not be {@code null} and must contain a non-{@code null} rating.
     * @return updated book representation.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or the request is invalid ({@code 400}).
     */
    BookResponse setAgeRating(Long bookId, AgeRatingUpdateRequest request);

    /**
     * Delete a book.
     *
     * @param bookId identifier of the book to delete.
     * @throws org.springframework.web.server.ResponseStatusException if the book is not found ({@code 404})
     *         or cannot be deleted because it is referenced by other records ({@code 409}).
     */
    void deleteBook(Long bookId);
}
