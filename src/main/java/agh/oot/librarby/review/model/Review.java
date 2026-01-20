package agh.oot.librarby.review.model;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.user.model.Reader;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_edition_id")
    private BookEdition bookEdition;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Boolean verified = false;

    // JPA no-args constructor
    protected Review() {
    }

    // Wywoływane automatycznie przed zapisem do bazy
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // Public constructor for creating new reviews
    public Review(Book book, Reader reader, Integer rating, String text, BookEdition bookEdition, Boolean verified) {
        this.book = book;
        this.reader = reader;
        this.rating = rating;
        this.text = text;
        this.bookEdition = bookEdition;
        this.verified = verified != null ? verified : false;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Reader getReader() {
        return reader;
    }

    public Integer getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Setters (business-appropriate)
    public void setBook(Book book) {
        this.book = book;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BookEdition getBookEdition() {
        return bookEdition;
    }

    public void setBookEdition(BookEdition bookEdition) {
        this.bookEdition = bookEdition;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
