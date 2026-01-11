package agh.oot.librarby.reservation.controller;

import agh.oot.librarby.reservation.dto.AssignCopyRequest;
import agh.oot.librarby.reservation.dto.ReservationRequest;
import agh.oot.librarby.reservation.dto.ReservationResponse;
import agh.oot.librarby.reservation.dto.ReservationSearchRequest;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.placeReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Find reservations filtered by optional readerId, bookId, status, with sorting and limit.
     * Example: GET /api/v1/reservations?readerId=1&bookId=2&status=PENDING&sortDirection=DESC&limit=10
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations(
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
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
    @PutMapping("/{reservationId}/assigned-book-copy")
    public ResponseEntity<ReservationResponse> assignCopyToReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody AssignCopyRequest request) {
        ReservationResponse response = reservationService.assignCopyToReservation(reservationId, request);
        return ResponseEntity.ok(response);
    }
}
