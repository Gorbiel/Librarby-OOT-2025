package agh.oot.librarby.book.model;

import agh.oot.librarby.publisher.model.Publisher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Year;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "book_editions")
public class BookEdition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "isbn", unique = true, nullable = false))
    private ISBN isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer pageCount;

    @Column(name = "publication_year")
    private Year publicationYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(nullable = false)
    private Locale language;

    public BookEdition() {
    }

    public BookEdition(ISBN isbn, Integer pageCount, Year publicationYear, Publisher publisher, Locale language) {
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(ISBN isbn) {
        this.isbn = isbn;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Year getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Year publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookEdition)) return false;
        BookEdition that = (BookEdition) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
