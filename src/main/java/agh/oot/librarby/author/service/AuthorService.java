package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;

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
}
