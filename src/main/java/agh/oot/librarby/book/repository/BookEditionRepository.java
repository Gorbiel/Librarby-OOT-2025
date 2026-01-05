package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookEditionRepository extends JpaRepository<BookEdition, Long> {
    Optional<BookEdition> findByIsbn(ISBN isbn);

    List<BookEdition> findByBookId(Long bookId);

    boolean existsByIsbn(ISBN isbn);
}
