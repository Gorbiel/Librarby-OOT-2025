package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.Genre;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = "authors")
    List<Book> findAll();

    @EntityGraph(attributePaths = "authors")
    Optional<Book> findById(Long id);

//    @EntityGraph(attributePaths = "authors")
//    List<Book> findByAgeRating(AgeRating ageRating);

//    @EntityGraph(attributePaths = "authors")
//    List<Book> findByTitleContainingIgnoreCase(String title);

//    @EntityGraph(attributePaths = "authors")
//    List<Book> findByGenre(Genre genre);

//    @EntityGraph(attributePaths = "authors")
//    List<Book> findByAuthors_Id(Long authorId);

    @EntityGraph(attributePaths = "authors")
    @Query(
            "select distinct b from Book b left join b.authors a " +
            "where (:title is null or lower(b.title) like lower(concat('%', :title, '%'))) " +
            "and (:genre is null or :genre member of b.genres) " +
            "and (:authorId is null or a.id = :authorId) " +
            "and (:ageRating is null or b.ageRating = :ageRating)"
    )
    List<Book> findByFiltered(
            @Param("title") String title,
            @Param("genre") Genre genre,
            @Param("authorId") Long authorId,
            @Param("ageRating") AgeRating ageRating
    );
}
