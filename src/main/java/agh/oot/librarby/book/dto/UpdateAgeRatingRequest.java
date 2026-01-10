package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.AgeRating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload to overwrite a book's age rating")
public record UpdateAgeRatingRequest(
        @NotNull
        @Schema(description = "New age rating", example = "ADULT", requiredMode = Schema.RequiredMode.REQUIRED)
        AgeRating ageRating
) {}
