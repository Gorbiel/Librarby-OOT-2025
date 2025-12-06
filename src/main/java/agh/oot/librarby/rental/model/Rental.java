package agh.oot.librarby.rental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACJE PRZEZ ID (Modułowość) ---

    @NotNull
    @Column(name = "copy_id", nullable = false, updatable = false)
    private Long exactBookCopyId;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    // --- CZAS ---

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime rentedAt;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime returnedAt;

    // --- STAN ---

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    // JPA no-args constructor
    protected Rental() {
    }

    // Public constructor for creating new Rental
    public Rental(Long exactBookCopyId, Long userId, LocalDate dueDate, RentalStatus status) {
        this.exactBookCopyId = exactBookCopyId;
        this.userId = userId;
        this.dueDate = dueDate;
        this.status = status;
        this.rentedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExactBookCopyId() {
        return exactBookCopyId;
    }

    public void setExactBookCopyId(Long exactBookCopyId) {
        this.exactBookCopyId = exactBookCopyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getRentedAt() {
        return rentedAt;
    }

    public void setRentedAt(LocalDateTime rentedAt) {
        this.rentedAt = rentedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rental)) return false;
        Rental rental = (Rental) o;
        return id != null && id.equals(rental.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
