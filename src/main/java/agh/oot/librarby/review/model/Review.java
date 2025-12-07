package agh.oot.librarby.review.model;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.user.model.Reader;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // JPA no-args constructor
    protected Review() {
    }

    // Public constructor for creating new reviews
    public Review(Book book, Reader reader, Integer rating, String text) {
        this.book = book;
        this.reader = reader;
        this.rating = rating;
        this.text = text;
        this.createdAt = Instant.now();
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
