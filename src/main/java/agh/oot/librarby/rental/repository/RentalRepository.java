package agh.oot.librarby.rental.repository;

import agh.oot.librarby.rental.model.Rental;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
