package agh.oot.librarby.review.controller;

import agh.oot.librarby.auth.model.CustomUserDetails;
import agh.oot.librarby.exception.ApiErrorResponse;
import agh.oot.librarby.review.dto.CreateReviewRequest;
import agh.oot.librarby.review.dto.ReviewResponse;
import agh.oot.librarby.review.dto.UpdateReviewRequest;
import agh.oot.librarby.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reviews", description = "Endpoints for managing book reviews")
@RestController
@RequestMapping(
        path = "/api/v1/books/{bookId}/reviews",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create a review", description = "Creates a new review for a specific book. Readers can create reviews only for themselves, admins can create for anyone.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – insufficient privileges",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book or book edition not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@securityExpressions.isAdminOrOwner(#request.readerId())")
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "Book ID", example = "5", required = true)
            @PathVariable Long bookId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Review data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewRequest.class))
            )
            @RequestBody @Valid CreateReviewRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReviewResponse response = reviewService.createReview(bookId, request.readerId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a review", description = "Updates an existing review. Only the review owner or admins/librarians can update. Supports partial updates.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – not the owner or insufficient privileges",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review or book edition not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict – book edition already assigned or does not belong to the book",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{reviewId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@securityExpressions.isReviewOwnerOrPrivileged(#reviewId, authentication.principal.id, authentication)")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Book ID", example = "5", required = true)
            @PathVariable Long bookId,

            @Parameter(description = "Review ID", example = "1", required = true)
            @PathVariable Long reviewId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Review update data (partial)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateReviewRequest.class))
            )
            @RequestBody @Valid UpdateReviewRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReviewResponse response = reviewService.updateReview(bookId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a review", description = "Deletes an existing review. Only the review owner or admins/librarians can delete.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Review deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – not the owner or insufficient privileges",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("@securityExpressions.isReviewOwnerOrPrivileged(#reviewId, authentication.principal.id, authentication)")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Book ID", example = "5", required = true)
            @PathVariable Long bookId,

            @Parameter(description = "Review ID", example = "1", required = true)
            @PathVariable Long reviewId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        reviewService.deleteReview(bookId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
