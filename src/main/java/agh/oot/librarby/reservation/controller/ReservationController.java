package agh.oot.librarby.reservation.controller;

import agh.oot.librarby.reservation.dto.AssignCopyRequest;
import agh.oot.librarby.reservation.dto.ReservationRequest;
import agh.oot.librarby.reservation.dto.ReservationResponse;
import agh.oot.librarby.reservation.dto.ReservationSearchRequest;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reservations", description = "Endpoints for managing book reservations")
@RequestMapping(path = "/api/v1/reservations", produces = "application/json")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Create a new reservation",
            description = "Place a new reservation for a book"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid reservation request"
            )
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.placeReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Find reservations filtered by optional readerId, bookId, status, with sorting and limit.
     * Example: GET /api/v1/reservations?readerId=1&bookId=2&status=PENDING&sortDirection=DESC&limit=10
     */
    @Operation(
            summary = "Find reservations",
            description = "Retrieve reservations filtered by optional parameters"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters"
            )
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations(
            @Parameter(description = "Filter by reader ID", example = "1")
            @RequestParam(required = false) Long readerId,
            @Parameter(description = "Filter by book ID", example = "2")
            @RequestParam(required = false) Long bookId,
            @Parameter(description = "Filter by reservation status", example = "PENDING")
            @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @Parameter(description = "Limit number of results", example = "10")
            @RequestParam(required = false) Integer limit) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest(
                readerId, bookId, status, sortDirection, limit
        );

        List<ReservationResponse> reservations = reservationService.findReservations(searchRequest);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Get a specific reservation by id
     *
     * @param reservationId the id of the reservation
     * @return the reservation details
     */
    @Operation(
            summary = "Get reservation by ID",
            description = "Retrieve a specific reservation by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found"
            )
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long reservationId) {
        ReservationResponse reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Cancel a reservation by id
     *
     * @param reservationId the id of reservation to cancel
     * @return 204 No Content on successful cancellation
     */
    @Operation(
            summary = "Cancel a reservation",
            description = "Cancel a specific reservation by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Reservation cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation not found"
            )
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign an exact book copy to a specific reservation
     *
     * @param reservationId the ID of the reservation to assign the copy to
     * @param request       the assignment request containing exactBookCopyId and holdExpirationDate
     * @return the updated reservation with ASSIGNED status
     */
    @Operation(
            summary = "Assign a book copy to a reservation",
            description = "Assign an exact book copy to a specific reservation"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book copy assigned to reservation successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reservation or Book Copy not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid assignment request"
            )
    })
    @PutMapping("/{reservationId}/assigned-book-copy")
    public ResponseEntity<ReservationResponse> assignCopyToReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody AssignCopyRequest request) {
        ReservationResponse response = reservationService.assignCopyToReservation(reservationId, request);
        return ResponseEntity.ok(response);
    }
}
