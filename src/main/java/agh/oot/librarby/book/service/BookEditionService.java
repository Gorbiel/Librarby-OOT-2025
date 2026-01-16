package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.dto.UpdateBookEditionRequest;

/**
 * Service interface for operations on book editions.
 *
 * <p>Implementations are expected to perform validation and translate domain errors into
 * appropriate runtime exceptions (for example {@code org.springframework.web.server.ResponseStatusException}
 * for HTTP-aware controllers).
 *
 * <p>Methods in this interface work with DTOs defined in {@code agh.oot.librarby.book.dto} and
 * should not expose internal JPA entities to callers.
 */
public interface BookEditionService {

    /**
     * Retrieve a single book edition by its identifier.
     *
     * @param bookEditionId identifier of the book edition to fetch (must not be {@code null}).
     * @return DTO representing the book edition.
     * @throws org.springframework.web.server.ResponseStatusException if the edition is not found (should map to HTTP 404).
     */
    BookEditionResponse getBookEdition(long bookEditionId);

    /**
     * Update an existing book edition.
     *
     * <p>Only fields provided in {@code request} should be applied. Implementations are expected
     * to validate business constraints (for example unique ISBN) and to throw an appropriate
     * runtime exception when a constraint is violated.
     *
     * @param id      identifier of the edition to update (must not be {@code null}).
     * @param request DTO carrying update values; individual fields may be {@code null} to indicate no change.
     * @return updated edition DTO.
     * @throws org.springframework.web.server.ResponseStatusException    if the edition (or referenced resources such as publisher) is not found
     *                                                                   or request validation fails (map to HTTP 400/404).
     * @throws agh.oot.librarby.exception.ResourceAlreadyExistsException if update would violate uniqueness constraints (for example ISBN already exists).
     */
    BookEditionResponse updateBookEditionById(Long id, UpdateBookEditionRequest request);

    /**
     * Create a new book edition for an existing book.
     *
     * <p>Performs validation of the input DTO (language format, ISBN uniqueness, presence of referenced
     * entities such as book and publisher). Newly created edition should be persisted and returned
     * as a DTO containing its generated identifier.
     *
     * @param request DTO with required data for creating an edition; must not be {@code null}.
     * @return created edition DTO.
     * @throws org.springframework.web.server.ResponseStatusException if validation fails or referenced entities are missing (map to HTTP 400/404).
     */
    BookEditionResponse createBookEdition(CreateBookEditionRequest request);

    /**
     * Delete an existing book edition.
     *
     * <p>Implementations should prevent deletion when the edition is referenced by other
     * domain records (for example exact book copies). In such a case an appropriate
     * runtime exception should be thrown (for example {@code IllegalStateException} or
     * {@code org.springframework.web.server.ResponseStatusException} with HTTP 409).
     *
     * @param id identifier of the edition to delete.
     * @throws org.springframework.web.server.ResponseStatusException if the edition is not found (HTTP 404)
     *                                                                or cannot be deleted because it is referenced (HTTP 409).
     */
    void deleteBookEdition(long id);
}
