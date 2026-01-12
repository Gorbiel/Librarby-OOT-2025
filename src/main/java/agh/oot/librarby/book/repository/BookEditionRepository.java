package agh.oot.librarby.book.repository;


import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookEditionRepository extends JpaRepository<BookEdition, Long> {
    boolean existsByIsbn(ISBN isbn);

    boolean existsByBook(Book book);

    List<BookEdition> getAllByBook(Book book);
}
