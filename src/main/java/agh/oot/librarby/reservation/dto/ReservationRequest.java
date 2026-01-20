package agh.oot.librarby.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReservationRequest(
        @Schema(description = "ID of the reader making the reservation", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        Long readerId,

        @Schema(description = "ID of the book to be reserved", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        Long bookId
) {}
