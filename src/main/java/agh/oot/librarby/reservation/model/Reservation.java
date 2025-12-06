package agh.oot.librarby.reservation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Long bookId; // Reservation for a Work (not for Edition/Copy)

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Long assignedExactBookCopyId;

    private LocalDateTime holdExpirationDate;

    protected Reservation() {
    }

    public Reservation(Long bookId, Long userId, ReservationStatus status) {
        this.bookId = bookId;
        this.userId = userId;
        this.status = status;
        this.createdAt = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getAssignedExactBookCopyId() {
        return assignedExactBookCopyId;
    }

    public void setAssignedExactBookCopyId(Long assignedExactBookCopyId) {
        this.assignedExactBookCopyId = assignedExactBookCopyId;
    }

    public LocalDateTime getHoldExpirationDate() {
        return holdExpirationDate;
    }

    public void setHoldExpirationDate(LocalDateTime holdExpirationDate) {
        this.holdExpirationDate = holdExpirationDate;
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
