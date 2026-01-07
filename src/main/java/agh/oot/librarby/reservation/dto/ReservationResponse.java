package agh.oot.librarby.reservation.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        Long bookId,
        Long readerId,
        String status,
        Instant createdAt,
        Long assignedCopyId,           // can be null
        LocalDate holdExpirationDate   // can be null
) {
}
