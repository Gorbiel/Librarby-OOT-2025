package agh.oot.librarby.reservation.model;

public enum ReservationStatus {
    PENDING,    // User is waiting in the queue (FIFO)
    ASSIGNED,   // A copy has returned and was set aside for this user (awaiting pickup)
    COMPLETED,  // User has picked up the book (converted to a Rental)
    CANCELLED,  // User cancelled the reservation
    EXPIRED     // User did not pick up in time (reservation moves to the next user)
}