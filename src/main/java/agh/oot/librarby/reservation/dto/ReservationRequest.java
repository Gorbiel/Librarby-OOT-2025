package agh.oot.librarby.reservation.dto;

public record ReservationRequest(
        Long readerId,
        Long bookId
) {
}
