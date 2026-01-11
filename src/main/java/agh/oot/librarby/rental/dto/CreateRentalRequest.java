package agh.oot.librarby.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request payload for creating a new rental")
public record CreateRentalRequest(

        @Schema(description = "Reader ID (same as user account ID)", example = "2137", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long readerId,

        @Schema(description = "Exact book copy ID to rent", example = "420", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long copyId,

        @Schema(description = "Due date of the rental (must be in the future)", example = "2026-02-01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Future
        LocalDate dueDate
) {}
