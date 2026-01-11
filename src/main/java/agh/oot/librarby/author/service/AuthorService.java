package agh.oot.librarby.author.service;

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
}
