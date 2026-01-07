package agh.oot.librarby.reservation.service;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.reservation.dto.ReservationRequest;
import agh.oot.librarby.reservation.dto.ReservationResponse;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.reservation.repository.ReservationRepository;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import agh.oot.librarby.reservation.model.Reservation;

import java.time.Instant;

@Service
public class ReservationService {
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(ReaderRepository readerRepository, BookRepository bookRepository, ReservationRepository reservationRepository) {
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
    }

    @PreAuthorize("#request.readerId == authentication.principal.id or hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Transactional
    public ReservationResponse placeReservation(ReservationRequest request) {
        Reader targetReader = readerRepository.findById(request.readerId())
                .orElseThrow(() -> new EntityNotFoundException("Reader not found"));

        Book targetBook = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        reservationRepository.findByReaderAndBookAndStatus(
                targetReader,
                targetBook,
                ReservationStatus.PENDING
        ).ifPresent(r -> {
            throw new IllegalStateException(String.format(
                    "Reader %d has pending reservation for book '%d' (reservation ID: %d, created at: %s)",
                    r.getReader().getId(),
                    r.getBook().getId(),
                    r.getId(),
                    r.getCreatedAt()
            ));
        });

        Reservation reservation = new Reservation(targetBook, targetReader);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(Instant.now());

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        Long assignedCopyId = null;
        if (reservation.getAssignedExactBookCopy() != null) {
            assignedCopyId = reservation.getAssignedExactBookCopy().getId();
        }

        Long bookId = reservation.getBook() != null ? reservation.getBook().getId() : null;
        Long readerId = reservation.getReader() != null ? reservation.getReader().getId() : null;
        String status = reservation.getStatus() != null ? reservation.getStatus().name() : null;

        return new ReservationResponse(
                reservation.getId(),
                bookId,
                readerId,
                status,
                reservation.getCreatedAt(),
                assignedCopyId,
                reservation.getHoldExpirationDate()
        );
    }
}
