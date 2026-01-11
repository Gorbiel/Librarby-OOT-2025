// java
package agh.oot.librarby.rental.model;

import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.book.model.ExactBookCopy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@NamedEntityGraph(
        name = "Rental.withDetails",
        attributeNodes = {
                @NamedAttributeNode("reader"),
                @NamedAttributeNode(value = "exactBookCopy", subgraph = "copySubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "copySubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "bookEdition", subgraph = "editionSubgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "editionSubgraph",
                        attributeNodes = { @NamedAttributeNode("book") }
                )
        }
)
@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "copy_id", nullable = false, updatable = false)
    private ExactBookCopy exactBookCopy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false, updatable = false)
    private Reader reader;

    @NotNull
    @Column(name = "rented_at", nullable = false, updatable = false)
    private Instant rentedAt;

    @PrePersist
    void prePersist() { if (rentedAt == null) rentedAt = Instant.now(); }


    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    protected Rental() {
    }

    public Rental(ExactBookCopy exactBookCopy, Reader reader, LocalDate dueDate, RentalStatus status) {
        if (exactBookCopy == null || reader == null || dueDate == null || status == null) {
            throw new IllegalArgumentException("exactBookCopy, reader, dueDate and status must not be null");
        }
        this.exactBookCopy = exactBookCopy;
        this.reader = reader;
        this.dueDate = dueDate;
        this.status = status;
        this.rentedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public ExactBookCopy getExactBookCopy() {
        return exactBookCopy;
    }

    public void setExactBookCopy(ExactBookCopy exactBookCopy) {
        this.exactBookCopy = exactBookCopy;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Instant getRentedAt() {
        return rentedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rental rental)) return false;
        return id != null && id.equals(rental.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
