package agh.oot.librarby.review.controller;

import agh.oot.librarby.exception.ApiErrorResponse;
import agh.oot.librarby.review.dto.MultipleReviewsResponse;
import agh.oot.librarby.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reviews", description = "Endpoints for managing book reviews")
@RestController
@RequestMapping(
        path = "/api/v1/reviews",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GlobalReviewController {
    private final ReviewService reviewService;

    public GlobalReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get all reviews with filters",
            description = "Retrieves a list of reviews with optional filters and sorting. Public endpoint.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviews retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MultipleReviewsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<MultipleReviewsResponse> getAllReviews(
            @Parameter(description = "Filter by reader ID", example = "10")
            @RequestParam(required = false) Long readerId,

            @Parameter(description = "Filter by book ID", example = "5")
            @RequestParam(required = false) Long bookId,

            @Parameter(description = "Filter by book edition ID", example = "15")
            @RequestParam(required = false) Long bookEditionId,

            @Parameter(description = "Maximum number of results to return", example = "20")
            @RequestParam(required = false) Integer limit,

            @Parameter(description = "Sort direction by creation date (asc or desc)", example = "desc")
            @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        MultipleReviewsResponse response = reviewService.getAllReviews(
                readerId, bookId, bookEditionId, limit, sortDirection
        );
        return ResponseEntity.ok(response);
    }
}

