package agh.oot.librarby.rental.controller;

import agh.oot.librarby.auth.model.CustomUserDetails;
import agh.oot.librarby.exception.ApiErrorResponse;
import agh.oot.librarby.rental.dto.CreateRentalRequest;
import agh.oot.librarby.rental.dto.ExtendRentalRequest;
import agh.oot.librarby.rental.dto.MultipleRentalsResponse;
import agh.oot.librarby.rental.dto.RentalResponse;
import agh.oot.librarby.rental.model.RentalStatus;
import agh.oot.librarby.rental.service.RentalService;
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
import org.springframework.web.server.ResponseStatusException;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rentals", description = "Endpoints for managing rentals")
@RestController
@RequestMapping(
        path = "/api/v1/rentals",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // ------------------------------------------------------------
    // GET /api/v1/rentals?readerId=&bookId=&status=&active=
    // ------------------------------------------------------------

    @Operation(
            summary = "Get rentals",
            description = """
                    Retrieves rentals with optional filters.
                    Admin/Librarian: can view all rentals and filter freely.
                    Reader: must provide readerId equal to their own id.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rentals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MultipleRentalsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – insufficient privileges or readerId mismatch/missing",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN') or hasRole('READER')")
    public ResponseEntity<MultipleRentalsResponse> getRentals(
            @Parameter(description = "Filter by reader ID (required for READER role)", example = "123")
            @RequestParam(value = "readerId", required = false) Long readerId,

            @Parameter(description = "Filter by book ID (title-level)", example = "12")
            @RequestParam(value = "bookId", required = false) Long bookId,

            @Parameter(description = "Filter by rental status", example = "ACTIVE")
            @RequestParam(value = "status", required = false) RentalStatus status,

            @Parameter(description = "Filter by active flag: true = not returned, false = returned", example = "true")
            @RequestParam(value = "active", required = false) Boolean active,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        if (isReader(principal)) {
            if (readerId == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Readers must provide readerId");
            }
            if (!principal.getId().equals(readerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        }

        MultipleRentalsResponse body = rentalService.getRentals(readerId, bookId, status, active);
        return ResponseEntity.ok(body);
    }

    // ------------------------------------------------------------
    // GET /api/v1/rentals/{rentalId}
    // ------------------------------------------------------------

    @Operation(
            summary = "Get rental by ID",
            description = "Retrieves a rental by its ID. Admin/Librarian can view any rental. Reader can view only rentals that belong to them."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rental retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – rental does not belong to the reader",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rental not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{rentalId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN') or hasRole('READER')")
    public ResponseEntity<RentalResponse> getRentalById(
            @Parameter(description = "Rental ID", example = "1001", required = true)
            @PathVariable("rentalId") Long rentalId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        RentalResponse rental = rentalService.getRentalById(rentalId);

        if (isReader(principal) && !principal.getId().equals(rental.readerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return ResponseEntity.ok(rental);
    }

    // ------------------------------------------------------------
    // POST /api/v1/rentals
    // ------------------------------------------------------------

    @Operation(
            summary = "Create rental",
            description = "Creates a new rental for a reader and an exact book copy. Requires admin or librarian privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Rental created successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or copy not available",
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
                    description = "Reader or copy not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<RentalResponse> createRental(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Rental creation payload",
                    content = @Content(schema = @Schema(implementation = CreateRentalRequest.class))
            )
            @RequestBody @Valid CreateRentalRequest request
    ) {
        RentalResponse created = rentalService.createRental(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------------------------------------------------
    // POST /api/v1/rentals/{rentalId}/return
    // ------------------------------------------------------------

    @Operation(
            summary = "Return rental",
            description = "Marks a rental as returned and updates the copy status. Requires admin or librarian privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rental returned successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Rental already returned or invalid state",
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
                    description = "Rental not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/{rentalId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<RentalResponse> returnRental(
            @Parameter(description = "Rental ID", example = "1001", required = true)
            @PathVariable("rentalId") Long rentalId
    ) {
        RentalResponse updated = rentalService.returnRental(rentalId);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // PATCH /api/v1/rentals/{rentalId}
    // ------------------------------------------------------------

    @Operation(
            summary = "Extend rental due date",
            description = "Extends the due date for an active rental. Requires admin or librarian privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rental due date extended successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid due date or rental state",
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
                    description = "Rental not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{rentalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<RentalResponse> extendDueDate(
            @Parameter(description = "Rental ID", example = "1001", required = true)
            @PathVariable("rentalId") Long rentalId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "New due date",
                    content = @Content(schema = @Schema(implementation = ExtendRentalRequest.class))
            )
            @RequestBody @Valid ExtendRentalRequest request
    ) {
        RentalResponse updated = rentalService.extendDueDate(rentalId, request);
        return ResponseEntity.ok(updated);
    }

    private boolean isReader(CustomUserDetails principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_READER".equals(a.getAuthority()));
    }
}
