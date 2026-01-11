package agh.oot.librarby.notification.model;

import agh.oot.librarby.reservation.model.Reservation;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reservation_outbox")
public class ReservationEventOutbox {
    @Id
    private UUID id = UUID.randomUUID();

    private Long reservationId;
    private Long readerId;

    @Enumerated(EnumType.STRING)
    private ReservationEventType eventType;

    private String bookTitle;
    private LocalDate holdExpirationDate;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Instant createdAt;
    private int retryCount;

    private ReservationEventOutbox(Reservation res, ReservationEventType eventType) {
        this.reservationId = res.getId();
        this.readerId = res.getReader().getId();
        this.eventType = eventType;
        this.bookTitle = res.getBook().getTitle();
        this.holdExpirationDate = res.getHoldExpirationDate();
        this.status = OutboxStatus.PENDING;
        this.createdAt = Instant.now();
        this.retryCount = 0;
    }

    public static ReservationEventOutbox build(Reservation res, ReservationEventType eventType) {
        return new ReservationEventOutbox(res, eventType);
    }


    protected ReservationEventOutbox() {
    }

    public UUID getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public ReservationEventType getEventType() {
        return eventType;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getHoldExpirationDate() {
        return holdExpirationDate;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getRetryCount() {
        return retryCount;
    }


}