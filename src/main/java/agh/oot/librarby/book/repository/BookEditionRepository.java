package agh.oot.librarby.book.repository;


import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEditionRepository extends JpaRepository<BookEdition, Long> {
    boolean existsByIsbn(ISBN isbn);
}
