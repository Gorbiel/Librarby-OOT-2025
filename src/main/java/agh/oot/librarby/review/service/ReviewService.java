package agh.oot.librarby.review.service;

import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.rental.repository.RentalRepository;
import agh.oot.librarby.review.dto.CreateReviewRequest;
import agh.oot.librarby.review.dto.MultipleReviewsResponse;
import agh.oot.librarby.review.dto.ReviewResponse;
import agh.oot.librarby.review.dto.UpdateReviewRequest;
import agh.oot.librarby.review.mapper.ReviewResponseMapper;
import agh.oot.librarby.review.model.Review;
import agh.oot.librarby.review.repository.ReviewRepository;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            verifyBookEditionBelongsToBook(bookEdition, bookId);
        }

        // Check if the reader has rented this book before (verified review)
        boolean verified = rentalRepository.hasReaderRentedBook(readerId, bookId);

        // Create and save review
        Review review = new Review(book, reader, request.rating(), request.text(), bookEdition, verified);
        Review savedReview = reviewRepository.save(review);

        return reviewResponseMapper.toDto(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long bookId, Long reviewId, UpdateReviewRequest request) {
        // Find review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        verifyReviewBelongsToBook(review, bookId);

        // Update rating if provided
        if (request.rating() != null) {
            review.setRating(request.rating());
        }

        // Update text if provided (even if null, to allow clearing the text)
        if (request.text() != null) {
            review.setText(request.text());
        }

        // Update book edition if provided and not already set
        if (request.bookEditionId() != null) {
            if (review.getBookEdition() != null) {
                throw new IllegalStateException("Book edition is already assigned to this review and cannot be changed");
            }

            BookEdition bookEdition = bookEditionRepository.findById(request.bookEditionId())
                    .orElseThrow(() -> new EntityNotFoundException("Book edition not found with id: " + request.bookEditionId()));

            verifyBookEditionBelongsToBook(bookEdition, bookId);

            review.setBookEdition(bookEdition);
        }

        Review updatedReview = reviewRepository.save(review);
        return reviewResponseMapper.toDto(updatedReview);
    }

    @Transactional
    public void deleteReview(Long bookId, Long reviewId) {
        // Find review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        verifyReviewBelongsToBook(review, bookId);

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long bookId, Long reviewId) {
        // Find review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        verifyReviewBelongsToBook(review, bookId);

        return reviewResponseMapper.toDto(review);
    }

    @Transactional(readOnly = true)
    public MultipleReviewsResponse getAllReviews(Long readerId, Long bookId, Long bookEditionId,
                                                 Integer limit, String sortDirection) {
        // Create sort order
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt");

        // Create pageable with limit
        Pageable pageable = limit != null && limit > 0
                ? PageRequest.of(0, limit, sort)
                : PageRequest.of(0, Integer.MAX_VALUE, sort);

        // Find reviews with filters
        List<Review> reviews = reviewRepository.findByFilters(readerId, bookId, bookEditionId, pageable);

        // Map to DTOs
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(reviewResponseMapper::toDto)
                .toList();

        return new MultipleReviewsResponse(reviewResponses);
    }

    private void verifyBookEditionBelongsToBook(BookEdition bookEdition, Long bookId) {
        if (!bookEdition.getBook().getId().equals(bookId)) {
            throw new IllegalArgumentException("Book edition does not belong to the specified book");
        }
    }

    private void verifyReviewBelongsToBook(Review review, Long bookId) {
        if (!review.getBook().getId().equals(bookId)) {
            throw new IllegalArgumentException("Review does not belong to the specified book");
        }
    }
}
