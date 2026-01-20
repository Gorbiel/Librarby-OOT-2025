package agh.oot.librarby.statistics.service;


import agh.oot.librarby.book.repository.ExactBookCopyRepository;

import agh.oot.librarby.rental.model.RentalStatus;

import agh.oot.librarby.review.repository.ReviewRepository;

import agh.oot.librarby.statistics.dto.*;

import org.springframework.data.domain.PageRequest;

import agh.oot.librarby.rental.repository.RentalRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.Instant;

import java.util.List;


@Service

public class StatisticsService {

    private final RentalRepository rentalRepository;

    private final ExactBookCopyRepository exactBookCopyRepository;

    private final ReviewRepository reviewRepository;


    public StatisticsService(RentalRepository rentalRepository, ExactBookCopyRepository exactBookCopyRepository, ReviewRepository reviewRepository) {

        this.rentalRepository = rentalRepository;

        this.exactBookCopyRepository = exactBookCopyRepository;

        this.reviewRepository = reviewRepository;

    }


    public List<BookPopularityDTO> getMostBorrowedBooks(Instant start, Instant end, int limit) {

        return rentalRepository.findMostBorrowedBooks(start, end, PageRequest.of(0, limit));

    }


    public AverageDurationDTO getAverageDurationForBook(Long bookId) {

        List<Object[]> rentals = rentalRepository.findRentalDatesByBookId(bookId);


        if (rentals.isEmpty()) {

            return new AverageDurationDTO(bookId, 0.0);

        }


        double totalSeconds = 0;

        int count = 0;


        for (Object[] dates : rentals) {

            Instant rentedAt = (Instant) dates[0];

            Instant returnedAt = (Instant) dates[1];

            long seconds = Duration.between(rentedAt, returnedAt).getSeconds();

            totalSeconds += seconds;

            count++;

        }


        double averageSeconds = totalSeconds / count;

        double averageDays = averageSeconds / (60 * 60 * 24);

        double roundedDays = Math.round(averageDays * 100.0) / 100.0;


        return new AverageDurationDTO(bookId, roundedDays);

    }


    public LateReturnRateDTO getLateReturnRateForReader(Long readerId) {

        List<RentalStatus> statuses = rentalRepository.findAllCompletedStatusesByReaderId(readerId);


        if (statuses.isEmpty()) {

            return new LateReturnRateDTO(readerId, 0.0);

        }


        long lateCount = statuses.stream()

                .filter(status -> status == RentalStatus.LATE)

                .count();


        double percentage = ((double) lateCount / statuses.size()) * 100.0;


        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;


        return new LateReturnRateDTO(readerId, roundedPercentage);

    }


    public AvailabilityRatioDTO getAvailabilityRatio(Long bookId) {

        List<Object[]> stats = exactBookCopyRepository.findAvailabilityStatsByBookId(bookId);


        Object[] result = stats.getFirst();

        Long totalCopies = (Long) result[0];

        Long availableCopies = (Long) result[1];


        double percentage = ((double) availableCopies / totalCopies) * 100.0;

        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;


        return new AvailabilityRatioDTO(bookId, roundedPercentage);

    }


    public List<TopReviewerDTO> getTopReviewers(int limit) {

        return reviewRepository.findTopReviewers(PageRequest.of(0, limit));

    }


    public AuthorStatisticsDTO getAuthorStatistics(Long authorId) {

        Double average = reviewRepository.findAverageRatingByAuthorId(authorId);


        if (average == null) {

            return new AuthorStatisticsDTO(authorId, 0.0);

        }


        double roundedAverage = Math.round(average * 100.0) / 100.0;


        return new AuthorStatisticsDTO(authorId, roundedAverage);

    }

}