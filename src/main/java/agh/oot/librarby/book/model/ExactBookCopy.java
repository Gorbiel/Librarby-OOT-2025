package agh.oot.librarby.book.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "exact_book_copies")
public class ExactBookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_edition_id", nullable = false, updatable = false)
    private BookEdition bookEdition;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status;

    protected ExactBookCopy() {
    }

    public ExactBookCopy(BookEdition bookEdition, CopyStatus status) {
        this.bookEdition = bookEdition;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public BookEdition getBookEdition() {
        return bookEdition;
    }

    public void setBookEdition(BookEdition bookEdition) {
        this.bookEdition = bookEdition;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public void setStatus(CopyStatus status) {
        this.status = status;
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExactBookCopy)) return false;
        ExactBookCopy that = (ExactBookCopy) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
