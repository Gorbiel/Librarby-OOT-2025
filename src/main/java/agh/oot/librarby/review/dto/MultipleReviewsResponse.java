package agh.oot.librarby.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response containing a list of reviews")
public record MultipleReviewsResponse(
        @Schema(description = "List of reviews")
        List<ReviewResponse> reviews
) {
}

