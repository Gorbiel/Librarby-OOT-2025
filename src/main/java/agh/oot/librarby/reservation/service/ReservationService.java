package agh.oot.librarby.reservation.service;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.ExactBookCopy;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import agh.oot.librarby.reservation.dto.AssignCopyRequest;
import agh.oot.librarby.reservation.dto.ReservationRequest;
import agh.oot.librarby.reservation.dto.ReservationResponse;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.reservation.repository.ReservationRepository;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.mapper.UserResponseMapper;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import agh.oot.librarby.reservation.model.Reservation;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final UserResponseMapper userResponseMapper;
    private final ExactBookCopyRepository exactBookCopyRepository;
    private final ReservationSecurity reservationSecurity;

    public ReservationService(ReaderRepository readerRepository,
                              BookRepository bookRepository,
                              ReservationRepository reservationRepository,
                              UserResponseMapper userResponseMapper,
                              ExactBookCopyRepository exactBookCopyRepository, ReservationSecurity reservationSecurity) {
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.userResponseMapper = userResponseMapper;
        this.exactBookCopyRepository = exactBookCopyRepository;
        this.reservationSecurity = reservationSecurity;
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

    // helper: extract principal id if available
    private Long extractPrincipalId(Authentication auth) {
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal == null) return null;
        try {
            Method getId = principal.getClass().getMethod("getId");
            Object idObj = getId.invoke(principal);
            if (idObj instanceof Number) return ((Number) idObj).longValue();
        } catch (Exception ignored) {
        }
        return null;
    }

    private boolean hasAdminRole(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            String ga = a.getAuthority();
            if ("ROLE_ADMIN".equals(ga) || "ROLE_LIBRARIAN".equals(ga)) return true;
        }
        return false;
    }

    /**
     * Find reservations filtered by optional readerId and/or bookId, ordered ascending by creation time.
     * If only readerId provided -> allowed for owner or ADMIN/LIBRARIAN.
     * If only bookId provided -> allowed only for ADMIN/LIBRARIAN.
     * If both provided -> allowed for owner (if principal matches readerId) or ADMIN/LIBRARIAN.
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(Long readerId, Long bookId) {
        if (readerId == null && bookId == null) {
            throw new IllegalArgumentException("At least one filter parameter (readerId or bookId) must be provided");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasAdminRole(auth);

        if (readerId != null) {
            if (!isAdmin) {
                Long principalId = extractPrincipalId(auth);
                if (principalId == null || !principalId.equals(readerId)) {
                    throw new AccessDeniedException("Not allowed to view reservations for this reader");
                }
            }
            if (bookId != null) {
                List<Reservation> reservations = reservationRepository.findByBookIdAndReaderIdOrderByCreatedAtAsc(bookId, readerId);
                return reservations.stream().map(this::mapToResponse).collect(Collectors.toList());
            } else {
                List<Reservation> reservations = reservationRepository.findByReaderIdOrderByCreatedAtAsc(readerId);
                return reservations.stream().map(this::mapToResponse).collect(Collectors.toList());
            }
        } else { // readerId == null && bookId != null
            if (!isAdmin) throw new AccessDeniedException("Not allowed to view reservations for books");
            List<Reservation> reservations = reservationRepository.findByBookIdOrderByCreatedAtAsc(bookId);
            return reservations.stream().map(this::mapToResponse).collect(Collectors.toList());
        }
    }

    /**
     * Get list of readers who reserved a specific book, ordered by reservation creation time (earliest first)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getReadersOrderedByReservationTimeForBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }

        List<Reservation> reservations = reservationRepository.findByBookIdOrderByCreatedAtAsc(bookId);

        return reservations.stream()
                .map(Reservation::getReader)
                .map(userResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all reservations for a specific reader
     */
    @Transactional(readOnly = true)
    @PreAuthorize("#readerId == authentication.principal.id or hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<ReservationResponse> getReservationsByReader(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            throw new EntityNotFoundException("Reader not found with id: " + readerId);
        }

        List<Reservation> reservations = reservationRepository.findByReaderId(readerId);

        return reservations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    /**
     * Get the oldest reservation for a specific book
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Transactional(readOnly = true)
    public ReservationResponse getOldestReservationForBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }

        Reservation oldestReservation = reservationRepository.findFirstByBookIdOrderByCreatedAtAsc(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No reservations found for book with id: " + bookId));

        return mapToResponse(oldestReservation);
    }

    /**
     * Cancel a reservation for a specific reservation id by changing status to CANCELLED
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        Long readerId = reservation.getReader().getId();
        // perform explicit authorization check: owner or admin/librarian
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasAdminRole(auth);
        if (!isAdmin) {
            Long principalId = extractPrincipalId(auth);
            if (principalId == null || !principalId.equals(readerId)) {
                throw new AccessDeniedException("Not allowed to cancel this reservation");
            }
        }

        // Only pending or assigned reservations can be cancelled (business rule)
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }


    /**
     * Assign an exact book copy to a specific reservation
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @Transactional
    public ReservationResponse assignCopyToReservation(Long reservationId, AssignCopyRequest request) {
        // Find the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        // Check if reservation is in PENDING status
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    String.format("Cannot assign copy to reservation with status %s. Only PENDING reservations can be assigned.",
                            reservation.getStatus()));
        }

        // Find the exact book copy
        ExactBookCopy exactBookCopy = exactBookCopyRepository.findById(request.exactBookCopyId())
                .orElseThrow(() -> new EntityNotFoundException("Exact book copy not found with id: " + request.exactBookCopyId()));

        // Verify that the book copy matches the book in the reservation
        Book bookFromCopy = exactBookCopy.getBookEdition().getBook();
        if (bookFromCopy == null || !bookFromCopy.getId().equals(reservation.getBook().getId())) {
            throw new IllegalArgumentException(
                    String.format("Book copy (book id: %s) does not match reservation book (book id: %s)",
                            bookFromCopy != null ? bookFromCopy.getId() : "null",
                            reservation.getBook().getId()));
        }

        // Assign the copy to the reservation
        reservation.setAssignedExactBookCopy(exactBookCopy);
        reservation.setStatus(ReservationStatus.ASSIGNED);
        reservation.setHoldExpirationDate(request.holdExpirationDate());

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }


}
