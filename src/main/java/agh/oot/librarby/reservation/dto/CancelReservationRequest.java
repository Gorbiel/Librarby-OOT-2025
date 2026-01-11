package agh.oot.librarby.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record CancelReservationRequest(
        @NotNull(message = "Reader ID is required")
        Long readerId,

        @NotNull(message = "Book ID is required")
        Long bookId
) {
}

