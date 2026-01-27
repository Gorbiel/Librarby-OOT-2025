package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ExactBookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExactBookCopyRepository extends JpaRepository<ExactBookCopy, Long> {
    boolean existsByBookEdition(BookEdition bookEdition);

    @Query("""
        SELECT 
            COUNT(e), 
            SUM(CASE WHEN e.status = 'AVAILABLE' THEN 1 ELSE 0 END)
        FROM ExactBookCopy e
        JOIN e.bookEdition be
        JOIN be.book b
        WHERE b.id = :bookId
    """)
        // Zwraca tablicę: [Całkowita liczba egzemplarzy (Long), Liczba dostępnych (Long)]
    List<Object[]> findAvailabilityStatsByBookId(@Param("bookId") Long bookId);
}

