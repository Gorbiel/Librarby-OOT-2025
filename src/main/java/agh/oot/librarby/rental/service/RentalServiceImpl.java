package agh.oot.librarby.rental.service;

import agh.oot.librarby.book.model.CopyStatus;
import agh.oot.librarby.book.model.ExactBookCopy;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import agh.oot.librarby.rental.dto.CreateRentalRequest;
import agh.oot.librarby.rental.dto.ExtendRentalRequest;
import agh.oot.librarby.rental.dto.MultipleRentalsResponse;
import agh.oot.librarby.rental.dto.RentalResponse;
import agh.oot.librarby.rental.mapper.RentalMapper;
import agh.oot.librarby.rental.model.Rental;
import agh.oot.librarby.rental.model.RentalStatus;
import agh.oot.librarby.rental.repository.RentalRepository;
import agh.oot.librarby.rental.repository.RentalSpecifications;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final ExactBookCopyRepository exactBookCopyRepository;
    private final ReaderRepository readerRepository;

    public RentalServiceImpl(
            RentalRepository rentalRepository,
            ExactBookCopyRepository exactBookCopyRepository,
            ReaderRepository readerRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.exactBookCopyRepository = exactBookCopyRepository;
        this.readerRepository = readerRepository;
    }

    @Override
    public MultipleRentalsResponse getRentals(Long readerId, Long bookId, RentalStatus status, Boolean active) {
        Specification<Rental> spec = Stream.of(
                        RentalSpecifications.hasReaderId(readerId),
                        RentalSpecifications.hasBookId(bookId),
                        RentalSpecifications.hasStatus(status),
                        RentalSpecifications.isActive(active)
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

        List<RentalResponse> rentals = rentalRepository.findAll(spec).stream()
                .map(RentalMapper::toResponse)
                .toList();

        return new MultipleRentalsResponse(rentals);
    }

    @Override
    public RentalResponse getRentalById(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        return RentalMapper.toResponse(rental);
    }

    @Override
    @Transactional
    public RentalResponse createRental(CreateRentalRequest request) {
        Reader reader = readerRepository.findById(request.readerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));

        ExactBookCopy copy = exactBookCopyRepository.findById(request.copyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exact book copy not found"));

        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Exact book copy is not available for rental");
        }

        LocalDate dueDate = request.dueDate();
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date must not be null");
        }
        // Optional: enforce dueDate >= today or in the future
        if (!dueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }

        // Business transition: copy becomes borrowed
        copy.setStatus(CopyStatus.BORROWED);

        Rental rental = new Rental(copy, reader, dueDate, RentalStatus.ACTIVE);

        // Save order: saving rental will persist FK to copy/reader; copy status update is in same TX
        Rental saved = rentalRepository.save(rental);

        return RentalMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RentalResponse returnRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        if (rental.getReturnedAt() != null) {
            throw new IllegalArgumentException("Rental has already been returned");
        }

        // Mark return time
        rental.setReturnedAt(Instant.now());

        // Determine status based on dueDate
        LocalDate today = LocalDate.now();
        if (today.isAfter(rental.getDueDate())) {
            rental.setStatus(RentalStatus.LATE);
        } else {
            rental.setStatus(RentalStatus.ON_TIME);
        }

        // Business transition: copy becomes available again
        ExactBookCopy copy = rental.getExactBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);

        Rental saved = rentalRepository.save(rental);
        return RentalMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RentalResponse extendDueDate(Long rentalId, ExtendRentalRequest request) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        if (rental.getReturnedAt() != null) {
            throw new IllegalArgumentException("Cannot extend due date for a returned rental");
        }

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active rentals can be extended");
        }

        LocalDate newDueDate = request.dueDate();
        if (newDueDate == null) {
            throw new IllegalArgumentException("Due date must not be null");
        }

        // "extend only" rule
        if (!newDueDate.isAfter(rental.getDueDate())) {
            throw new IllegalArgumentException("New due date must be after the current due date");
        }

        rental.setDueDate(newDueDate);

        Rental saved = rentalRepository.save(rental);
        return RentalMapper.toResponse(saved);
    }
}
