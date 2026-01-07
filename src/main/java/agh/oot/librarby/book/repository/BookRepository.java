package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
