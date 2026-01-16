package agh.oot.librarby.review.service;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.rental.repository.RentalRepository;
import agh.oot.librarby.review.dto.CreateReviewRequest;
import agh.oot.librarby.review.dto.ReviewResponse;
import agh.oot.librarby.review.mapper.ReviewResponseMapper;
import agh.oot.librarby.review.model.Review;
import agh.oot.librarby.review.repository.ReviewRepository;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final BookEditionRepository bookEditionRepository;
    private final ReaderRepository readerRepository;
    private final RentalRepository rentalRepository;
    private final ReviewResponseMapper reviewResponseMapper;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         BookEditionRepository bookEditionRepository,
                         ReaderRepository readerRepository,
                         RentalRepository rentalRepository,
                         ReviewResponseMapper reviewResponseMapper) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.bookEditionRepository = bookEditionRepository;
        this.readerRepository = readerRepository;
        this.rentalRepository = rentalRepository;
        this.reviewResponseMapper = reviewResponseMapper;
    }

    @Transactional
    public ReviewResponse createReview(Long bookId, Long readerId, CreateReviewRequest request) {
        // Find book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        // Find reader
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new EntityNotFoundException("Reader not found with id: " + readerId));

        // Find book edition if provided
        BookEdition bookEdition = null;
        if (request.bookEditionId() != null) {
            bookEdition = bookEditionRepository.findById(request.bookEditionId())
                    .orElseThrow(() -> new EntityNotFoundException("Book edition not found with id: " + request.bookEditionId()));

            // Verify that the book edition belongs to the specified book
            if (!bookEdition.getBook().getId().equals(bookId)) {
                throw new IllegalArgumentException("Book edition does not belong to the specified book");
            }
        }

        // Check if the reader has rented this book before (verified review)
        boolean verified = rentalRepository.hasReaderRentedBook(readerId, bookId);

        // Create and save review
        Review review = new Review(book, reader, request.rating(), request.text(), bookEdition, verified);
        Review savedReview = reviewRepository.save(review);

        return reviewResponseMapper.toDto(savedReview);
    }
}
