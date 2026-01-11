package agh.oot.librarby.rental.dto;

import agh.oot.librarby.rental.model.RentalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Rental data returned by the API")
public record RentalResponse(

        @Schema(description = "Rental ID", example = "1001")
        Long id,

        @Schema(description = "Reader ID (same as user account ID)", example = "2137")
        Long readerId,

        @Schema(description = "Exact book copy ID rented", example = "456")
        Long copyId,

        @Schema(description = "Book edition ID for the rented copy", example = "77")
        Long editionId,

        @Schema(description = "Book (title-level) ID for the rented copy", example = "12")
        Long bookId,

        @Schema(description = "Book title", example = "Dune")
        String bookTitle,

        @Schema(description = "Rental status", example = "ACTIVE")
        RentalStatus status,

        @Schema(description = "Due date (date only)", example = "2026-02-01")
        LocalDate dueDate,

        @Schema(description = "Rental creation timestamp", example = "2026-01-09T12:34:56")
        LocalDateTime rentedAt,

        @Schema(description = "Return timestamp (null if not returned yet)", example = "2026-01-20T10:15:00", nullable = true)
        LocalDateTime returnedAt
) {}
