package agh.oot.librarby.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for creating a review")
public record CreateReviewRequest(
        @NotNull(message = "Reader ID is required")
        @Schema(description = "Reader ID (must match authenticated user's ID unless admin)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        Long readerId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        @Schema(description = "Rating from 1 to 5", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer rating,

        @Schema(description = "Review text (optional)", example = "Great book!", nullable = true)
        String text,

        @Schema(description = "Book edition ID (optional)", example = "10", nullable = true)
        Long bookEditionId
) {
}
