package agh.oot.librarby.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservationResponse(
        @Schema(description = "ID of the reservation", example = "15")
        Long id,

        @Schema(description = "ID of the reserved book", example = "10")
        Long bookId,

        @Schema(description = "ID of the reader who made the reservation", example = "5")
        Long readerId,

        @Schema(description = "Current status of the reservation", example = "ACTIVE")
        String status,

        @Schema(description = "Timestamp when the reservation was created", example = "2024-05-01T12:34:56Z")
        Instant createdAt,

        @Schema(description = "ID of the assigned book copy, if any", example = "20")
        Long assignedCopyId,           // can be null

        @Schema(description = "Expiration date of the hold on the assigned book copy, if any", example = "2024-12-31")
        LocalDate holdExpirationDate   // can be null
) {}
