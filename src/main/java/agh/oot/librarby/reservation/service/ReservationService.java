package agh.oot.librarby.reservation.service;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.CopyStatus;
import agh.oot.librarby.book.model.ExactBookCopy;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import agh.oot.librarby.notification.model.ReservationEventOutbox;
import agh.oot.librarby.notification.model.ReservationEventType;
import agh.oot.librarby.notification.repository.ReservationEventOutboxRepository;
import agh.oot.librarby.reservation.dto.AssignCopyRequest;
import agh.oot.librarby.reservation.dto.ReservationRequest;
import agh.oot.librarby.reservation.dto.ReservationResponse;
import agh.oot.librarby.reservation.dto.ReservationSearchRequest;
import agh.oot.librarby.reservation.model.Reservation;
import agh.oot.librarby.reservation.model.ReservationStatus;
import agh.oot.librarby.reservation.repository.ReservationRepository;
import agh.oot.librarby.reservation.specification.ReservationSpecifications;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ExactBookCopyRepository exactBookCopyRepository;
    private final ReservationEventOutboxRepository reservationEventOutboxRepository;


    public ReservationService(ReaderRepository readerRepository,
                              BookRepository bookRepository,
                              ReservationRepository reservationRepository,
                              ExactBookCopyRepository exactBookCopyRepository, ReservationEventOutboxRepository reservationEventOutboxRepository) {
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.exactBookCopyRepository = exactBookCopyRepository;
        this.reservationEventOutboxRepository = reservationEventOutboxRepository;
    }

    @Transactional
    @PreAuthorize("#request.readerId == authentication.principal.id or hasAnyRole('ADMIN', 'LIBRARIAN')")
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
                    "Reader %d already has a pending reservation for book %d",
                    r.getReader().getId(), r.getBook().getId()));
        });

        Reservation reservation = new Reservation(targetBook, targetReader);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(Instant.now());

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }


    @Transactional(readOnly = true)
    @PreAuthorize("@reservationSecurity.canSearch(#searchRequest.readerId, #searchRequest.bookId, authentication.principal.id, authentication)")
    public List<ReservationResponse> findReservations(ReservationSearchRequest searchRequest) {
        if (searchRequest.readerId() == null && searchRequest.bookId() == null) {
            throw new IllegalArgumentException("At least one filter parameter (readerId or bookId) must be provided");
        }

        Specification<Reservation> spec = (root, query, criteriaBuilder) -> null;

        spec = spec.and(ReservationSpecifications.hasReaderId(searchRequest.readerId()));
        spec = spec.and(ReservationSpecifications.hasBookId(searchRequest.bookId()));
        spec = spec.and(ReservationSpecifications.hasStatus(searchRequest.status()));

        Sort sort = searchRequest.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        Pageable pageable = PageRequest.of(0, searchRequest.getLimit(), sort);

        return reservationRepository.findAll(spec, pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@reservationSecurity.isOwnerOrPrivileged(#reservationId, authentication.principal.id, authentication)")
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        ReservationStatus current = reservation.getStatus();
        if (current != ReservationStatus.PENDING && current != ReservationStatus.ASSIGNED) {
            throw new IllegalStateException("Only reservations with status PENDING or ASSIGNED can be cancelled (current: " + current + ")");
        }

        ExactBookCopy assignedCopy = reservation.getAssignedExactBookCopy();
        if (assignedCopy != null && assignedCopy.getStatus() == CopyStatus.RESERVED) {
            assignedCopy.setStatus(CopyStatus.AVAILABLE);
            exactBookCopyRepository.save(assignedCopy);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        ReservationEventOutbox eventOutbox = ReservationEventOutbox.build(reservation, ReservationEventType.CANCELLED);
        reservationEventOutboxRepository.save(eventOutbox);
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@reservationSecurity.isOwnerOrPrivileged(#reservationId, authentication.principal.id, authentication)")
    public ReservationResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        return mapToResponse(reservation);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ReservationResponse assignCopyToReservation(Long reservationId, AssignCopyRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservations can be assigned.");
        }

        ExactBookCopy copy = exactBookCopyRepository.findById(request.exactBookCopyId())
                .orElseThrow(() -> new EntityNotFoundException("Copy not found"));

        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Copy must be AVAILABLE to be assigned to a reservation (current status: " + copy.getStatus() + ")");
        }

        Book bookFromCopy = copy.getBookEdition().getBook();
        if (bookFromCopy == null || !bookFromCopy.getId().equals(reservation.getBook().getId())) {
            throw new IllegalArgumentException("Copy book does not match reservation book");
        }

        copy.setStatus(CopyStatus.RESERVED);
        exactBookCopyRepository.save(copy);

        reservation.setAssignedExactBookCopy(copy);
        reservation.setStatus(ReservationStatus.ASSIGNED);
        reservation.setHoldExpirationDate(request.holdExpirationDate());

        ReservationEventOutbox eventOutbox = ReservationEventOutbox.build(reservation, ReservationEventType.READY_FOR_PICKUP);
        reservationEventOutboxRepository.save(eventOutbox);

        return mapToResponse(reservationRepository.save(reservation));
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        Long assignedCopyId = reservation.getAssignedExactBookCopy() != null ?
                reservation.getAssignedExactBookCopy().getId() : null;

        return new ReservationResponse(
                reservation.getId(),
                reservation.getBook().getId(),
                reservation.getReader().getId(),
                reservation.getStatus().name(),
                reservation.getCreatedAt(),
                assignedCopyId,
                reservation.getHoldExpirationDate()
        );
    }

}
