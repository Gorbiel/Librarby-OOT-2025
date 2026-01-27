package agh.oot.librarby.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CancelReservationRequest(
        @Schema(description = "ID of the reader cancelling the reservation", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Reader ID is required")
        Long readerId,

        @Schema(description = "ID of the book for which the reservation is to be cancelled", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Book ID is required")
        Long bookId
) {}
