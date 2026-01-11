package agh.oot.librarby.publisher.service;

import agh.oot.librarby.publisher.dto.MultiplePublishersResponse;
import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.dto.PublisherUpdateRequest;

/**
 * Publisher application service.
 * <p>
 * Defines publisher-related use-cases exposed to the web layer.
 * Implementations should remain HTTP-agnostic (no status codes / web exceptions),
 * and should throw domain/application exceptions instead.
 * </p>
 */
public interface PublisherService {

    /**
     * Retrieves all publishers, optionally filtered by a case-insensitive substring query.
     *
     * @param q optional filter; when {@code null} or blank, all publishers are returned.
     *          When present, the value is typically trimmed and used as a substring match.
     * @return response containing the (possibly empty) list of matching publishers.
     */
    MultiplePublishersResponse getAllPublishers(String q);

    /**
     * Retrieves a publisher by its id.
     *
     * @param publisherId publisher id (must not be {@code null}).
     * @return publisher data.
     * @throws RuntimeException if the publisher does not exist (implementation-specific),
     *                          preferably a domain/application exception (not an HTTP exception).
     */
    PublisherResponse getPublisherById(Long publisherId);

    /**
     * Creates a new publisher.
     *
     * @param request create request (must not be {@code null}).
     * @return created publisher data.
     * @throws agh.oot.librarby.exception.ResourceAlreadyExistsException
     *         if a publisher with the same name already exists.
     */
    PublisherResponse createPublisher(PublisherCreateRequest request);

    /**
     * Updates an existing publisher.
     *
     * @param publisherId publisher id (must not be {@code null}).
     * @param request update request (must not be {@code null}).
     * @return updated publisher data.
     * @throws RuntimeException if the publisher does not exist (implementation-specific),
     *                          preferably a domain/application exception (not an HTTP exception).
     */
    PublisherResponse updatePublisher(Long publisherId, PublisherUpdateRequest request);
}
