package agh.oot.librarby.review.repository;

import agh.oot.librarby.review.model.Review;
import agh.oot.librarby.statistics.dto.TopReviewerDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByIdAndReaderId(Long reviewId, Long readerId);

    /**
     * Find reviews with optional filters and pagination/sorting.
     * All parameters are optional (can be null).
     */
    @Query("""
                SELECT r FROM Review r
                WHERE (:readerId IS NULL OR r.reader.id = :readerId)
                AND (:bookId IS NULL OR r.book.id = :bookId)
                AND (:bookEditionId IS NULL OR r.bookEdition.id = :bookEditionId)
            """)
    List<Review> findByFilters(
            @Param("readerId") Long readerId,
            @Param("bookId") Long bookId,
            @Param("bookEditionId") Long bookEditionId,
            Pageable pageable
    );

    @Query("""
        SELECT new agh.oot.librarby.statistics.dto.TopReviewerDTO(
            read.id, 
            read.firstName, 
            read.lastName, 
            COUNT(rev)
        )
        FROM Review rev
        JOIN rev.reader read
        GROUP BY read.id, read.firstName, read.lastName
        ORDER BY COUNT(rev) DESC
    """)
    List<TopReviewerDTO> findTopReviewers(Pageable pageable);

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        JOIN r.book b
        JOIN b.authors a
        WHERE a.id = :authorId
    """)
    Double findAverageRatingByAuthorId(@Param("authorId") Long authorId);
}
