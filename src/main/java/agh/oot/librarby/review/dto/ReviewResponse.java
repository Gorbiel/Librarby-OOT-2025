package agh.oot.librarby.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Response payload containing review data")
public record ReviewResponse(
        @Schema(description = "Review ID", example = "1")
        Long id,

        @Schema(description = "Book ID", example = "5")
        Long bookId,

        @Schema(description = "Reader ID", example = "10")
        Long readerId,

        @Schema(description = "Book edition ID (optional)", example = "15", nullable = true)
        Long bookEditionId,

        @Schema(description = "Rating from 1 to 5", example = "5")
        Integer rating,

        @Schema(description = "Review text", example = "Great book!")
        String text,

        @Schema(description = "Review creation timestamp", example = "2026-01-16T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Whether the reader has rented this book from the library before writing the review", example = "true")
        Boolean verified
) {
}
