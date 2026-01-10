package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.Genre;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = "authors")
    List<Book> findAll();

    @EntityGraph(attributePaths = "authors")
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = "authors")
    List<Book> findByAgeRating(AgeRating ageRating);

    @EntityGraph(attributePaths = "authors")
    List<Book> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(attributePaths = "authors")
    List<Book> findByGenres(Genre genre);

    @EntityGraph(attributePaths = "authors")
    List<Book> findByAuthors_Id(Long authorId);
}
