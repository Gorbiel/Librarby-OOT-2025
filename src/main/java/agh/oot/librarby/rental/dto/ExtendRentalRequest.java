package agh.oot.librarby.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request payload for extending an active rental")
public record ExtendRentalRequest(

        @Schema(description = "New due date (must extend the current due date)", example = "2026-02-15", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Future
        LocalDate dueDate
) {}
