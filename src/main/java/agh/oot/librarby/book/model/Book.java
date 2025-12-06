package agh.oot.librarby.book.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Set<Genre> genres = new HashSet<>();


    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    protected Book() {
    }

    public Book(String title,
                Set<Genre> genres,
                AgeRating ageRating,
                Set<Author> authors) {
        this.title = title;
        this.genres = genres != null ? new HashSet<>(genres) : new HashSet<>();
        this.ageRating = ageRating;
        this.authors = authors != null ? new HashSet<>(authors) : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Genre> getGenres() {
        return new HashSet<>(genres);
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : new HashSet<>();
    }

    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        this.ageRating = ageRating;
    }

    public Set<Author> getAuthors() {
        return new HashSet<>(authors);
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors != null ? new HashSet<>(authors) : new HashSet<>();
    }

    // Equals and hashCode based on id (JPA entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book that = (Book) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
