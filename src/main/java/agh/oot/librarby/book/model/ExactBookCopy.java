package agh.oot.librarby.book.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "exact_book_copies")
public class ExactBookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique inventory number (barcode sticker attached to the book).
    // This is the business key of this object.
    @NotBlank
    @Column(nullable = false, unique = true)
    private String inventoryNumber;

    // Reference to the BookEdition id (we store only the ID to avoid loading the full BookEdition entity)
    @NotNull
    @Column(name = "book_edition_id", nullable = false, updatable = false)
    private Long bookEditionId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status;

    protected ExactBookCopy() {
    }

    // Public constructor (without id) - updated to accept bookEditionId
    public ExactBookCopy(String inventoryNumber, Long bookEditionId, CopyStatus status) {
        this.inventoryNumber = inventoryNumber;
        this.bookEditionId = bookEditionId;
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Long getBookEditionId() {
        return bookEditionId;
    }

    public void setBookEditionId(Long bookEditionId) {
        this.bookEditionId = bookEditionId;
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
