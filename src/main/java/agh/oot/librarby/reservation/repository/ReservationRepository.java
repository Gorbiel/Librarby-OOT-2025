package agh.oot.librarby.reservation.repository;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.reservation.model.Reservation;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.user.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReaderAndBookAndStatus(Reader reader, Book book, ReservationStatus status);
}
