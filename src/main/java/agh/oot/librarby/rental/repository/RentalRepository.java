package agh.oot.librarby.rental.repository;

import agh.oot.librarby.rental.model.Rental;
import agh.oot.librarby.rental.model.RentalStatus;
import agh.oot.librarby.statistics.dto.BookPopularityDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {

    @EntityGraph(value = "Rental.withDetails")
    List<Rental> findAll(Specification<Rental> spec);

    @Override
    @EntityGraph(value = "Rental.withDetails")
    List<Rental> findAll();

    @Override
    @EntityGraph(value = "Rental.withDetails")
    Optional<Rental> findById(Long id);

    /**
     * Check if a reader has ever rented a specific book (at title level).
     * Returns true if at least one rental exists (completed or not).
     */
    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                FROM Rental r
                JOIN r.exactBookCopy ebc
                JOIN ebc.bookEdition be
                WHERE r.reader.id = :readerId
                AND be.book.id = :bookId
            """)
    boolean hasReaderRentedBook(@Param("readerId") Long readerId, @Param("bookId") Long bookId);

    @Query("""
        SELECT new agh.oot.librarby.statistics.dto.BookPopularityDTO(b.title, COUNT(r))
        FROM Rental r
        JOIN r.exactBookCopy ebc
        JOIN ebc.bookEdition be
        JOIN be.book b
        WHERE r.rentedAt BETWEEN :startDate AND :endDate
        GROUP BY b.id, b.title
        ORDER BY COUNT(r) DESC
        LIMIT :topN
    """)
    List<BookPopularityDTO> findMostBorrowedBooks(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    @Query("""
        SELECT r.rentedAt, r.returnedAt
        FROM Rental r
        JOIN r.exactBookCopy ebc
        JOIN ebc.bookEdition be
        JOIN be.book b
        WHERE b.id = :bookId 
          AND r.returnedAt IS NOT NULL
    """)
    List<Object[]> findRentalDatesByBookId(@Param("bookId") Long bookId);

    @Query("""
        SELECT r.status
        FROM Rental r
        WHERE r.reader.id = :readerId 
          AND r.status <> 'ACTIVE'
    """)
    List<RentalStatus> findAllCompletedStatusesByReaderId(@Param("readerId") Long readerId);
}
