package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.AuthorCreateRequest;
import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.book.dto.MultipleBooksResponse;

/**
 * Author application service.
 * <p>
 * Provides read-only operations for browsing author data.
 */
public interface AuthorService {

    /**
     * Lists authors, optionally filtered by a free-text query.
     * <p>
     * If {@code q} is null/blank, returns all authors.
     * Otherwise performs a case-insensitive substring search across:
     * firstName, middleName, and lastName.
     *
     * @param q optional query string
     * @return wrapper DTO containing author list (possibly empty)
     */
    MultipleAuthorsResponse listAuthors(String q);

    /**
     * Retrieves a single author by ID.
     *
     * @param authorId unique identifier of the author
     * @return author DTO
     * @throws org.springframework.web.server.ResponseStatusException if author not found
     */
    AuthorResponse getAuthorById(Long authorId);

    /**
     * Lists all books linked to a given author.
     *
     * Public endpoint use case: browsing an author's bibliography.
     *
     * @param authorId unique identifier of the author
     * @return wrapper DTO containing all books written by the author (may be empty)
     * @throws org.springframework.web.server.ResponseStatusException if author does not exist (404)
     */
    MultipleBooksResponse getBooksByAuthorId(Long authorId);

    /**
     * Creates a new author.
     *
     * Authorization: ADMIN, LIBRARIAN.
     *
     * Validation:
     * - firstName is required (must not be blank)
     * - middleName and lastName are optional
     *
     * @param request author creation payload
     * @return created author as DTO
     */
    AuthorResponse createAuthor(AuthorCreateRequest request);
}
