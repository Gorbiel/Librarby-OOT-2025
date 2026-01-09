package agh.oot.librarby.reservation.repository;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.reservation.model.Reservation;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.user.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    Optional<Reservation> findByReaderAndBookAndStatus(Reader reader, Book book, ReservationStatus status);

    // Find all reservations for a specific book, ordered by creation time (earliest first)
    List<Reservation> findByBookIdOrderByCreatedAtAsc(Long bookId);

    // Find all reservations for a specific reader ordered by creation time
    List<Reservation> findByReaderIdOrderByCreatedAtAsc(Long readerId);

    // Find the first (earliest) reservation for a specific book
    Optional<Reservation> findFirstByBookIdOrderByCreatedAtAsc(Long bookId);

    // Find the oldest pending reservation for a specific book
    Optional<Reservation> findFirstByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);

    // Find reservations for a specific book and reader ordered by creation time
    List<Reservation> findByBookIdAndReaderIdOrderByCreatedAtAsc(Long bookId, Long readerId);
}
