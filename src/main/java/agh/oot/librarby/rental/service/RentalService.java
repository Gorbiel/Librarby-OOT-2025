package agh.oot.librarby.rental.service;

import agh.oot.librarby.rental.dto.CreateRentalRequest;
import agh.oot.librarby.rental.dto.ExtendRentalRequest;
import agh.oot.librarby.rental.dto.MultipleRentalsResponse;
import agh.oot.librarby.rental.dto.RentalResponse;
import agh.oot.librarby.rental.model.RentalStatus;

public interface RentalService {

    /**
     * List rentals with optional filters.
     * readerId is handled by controller authorization:
     *  - ADMIN/LIBRARIAN: any readerId or none
     *  - READER: allowed only if readerId == principal.id (and readerId must be provided)
     */
    MultipleRentalsResponse getRentals(Long readerId, Long bookId, RentalStatus status, Boolean active);

    /**
     * Get one rental by its id.
     * Authorization/ownership checks handled at controller level.
     */
    RentalResponse getRentalById(Long rentalId);

    /**
     * Create a new rental for an exact book copy.
     * Business rules:
     *  - copy must exist and be AVAILABLE
     *  - reader must exist
     *  - sets copy status to BORROWED
     */
    RentalResponse createRental(CreateRentalRequest request);

    /**
     * Mark rental as returned (idempotency is NOT allowed: returning twice is an error).
     * Business rules:
     *  - rental must exist and not be returned yet
     *  - sets returnedAt
     *  - sets rental status to ON_TIME or LATE based on dueDate
     *  - sets copy status to AVAILABLE
     */
    RentalResponse returnRental(Long rentalId);

    /**
     * Extend rental due date. Only extending is allowed.
     * Business rules:
     *  - rental must exist and not be returned
     *  - new dueDate must be strictly after current dueDate
     */
    RentalResponse extendDueDate(Long rentalId, ExtendRentalRequest request);
}
