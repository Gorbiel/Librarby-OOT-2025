package agh.oot.librarby.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
