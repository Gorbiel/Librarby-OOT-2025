package agh.oot.librarby.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssignCopyRequest(
        @NotNull(message = "Exact book copy ID is required")
        Long exactBookCopyId,

        @NotNull(message = "Hold expiration date is required")
        LocalDate holdExpirationDate
) {
}

