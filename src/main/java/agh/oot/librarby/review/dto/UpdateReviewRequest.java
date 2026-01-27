package agh.oot.librarby.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "Request payload for updating a review (partial update)")
public record UpdateReviewRequest(
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        @Schema(description = "New rating from 1 to 5 (optional)", example = "4", nullable = true)
        Integer rating,

        @Schema(description = "New review text (optional)", example = "Updated: Great book!", nullable = true)
        String text,

        @Schema(description = "Book edition ID to assign (optional, can only be set if not already assigned)", example = "10", nullable = true)
        Long bookEditionId
) {
}

