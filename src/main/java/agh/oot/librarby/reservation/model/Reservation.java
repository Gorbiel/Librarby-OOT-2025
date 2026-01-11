package agh.oot.librarby.reservation.model;

import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.ExactBookCopy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private Book book;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false, updatable = false)
    private Reader reader;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_exact_book_copy_id")
    private ExactBookCopy assignedExactBookCopy;

    private LocalDate holdExpirationDate;

    protected Reservation() {
    }

    public Reservation(Book book, Reader reader) {
        this.book = book;
        this.reader = reader;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
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

    // new accessors for the assigned exact copy
    public ExactBookCopy getAssignedExactBookCopy() {
        return assignedExactBookCopy;
    }

    public void setAssignedExactBookCopy(ExactBookCopy assignedExactBookCopy) {
        this.assignedExactBookCopy = assignedExactBookCopy;
    }

    public LocalDate getHoldExpirationDate() {
        return holdExpirationDate;
    }

    public void setHoldExpirationDate(LocalDate holdExpirationDate) {
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
