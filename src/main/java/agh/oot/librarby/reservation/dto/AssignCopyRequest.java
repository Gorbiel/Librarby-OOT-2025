package agh.oot.librarby.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssignCopyRequest(
        @Schema(description = "ID of the exact book copy to be assigned", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Exact book copy ID is required")
        Long exactBookCopyId,

        @Schema(description = "Expiration date of the hold on the assigned book copy", example = "2024-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Hold expiration date is required")
        LocalDate holdExpirationDate
) {
}

