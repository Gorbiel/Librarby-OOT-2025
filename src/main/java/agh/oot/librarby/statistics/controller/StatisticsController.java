package agh.oot.librarby.statistics.controller;



import agh.oot.librarby.statistics.dto.*;

import agh.oot.librarby.statistics.service.StatisticsService;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.*;


import java.time.Instant;

import java.util.List;


@RestController

@RequestMapping("/api/statistics")

public class StatisticsController {


    private final StatisticsService statisticsService;


    public StatisticsController(StatisticsService statisticsService) {

        this.statisticsService = statisticsService;

    }


    @GetMapping("/most-borrowed")

    public List<BookPopularityDTO> getMostBorrowed(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,

            @RequestParam(defaultValue = "10") int limit

    ) {

        return statisticsService.getMostBorrowedBooks(startDate, endDate, limit);

    }


    @GetMapping("/book/{bookId}/average-duration")

    public AverageDurationDTO getAverageDuration(@PathVariable Long bookId) {

        return statisticsService.getAverageDurationForBook(bookId);

    }


    @GetMapping("/reader/{readerId}/late-return-rate")

    public LateReturnRateDTO getLateReturnRate(@PathVariable Long readerId) {

        return statisticsService.getLateReturnRateForReader(readerId);

    }


    @GetMapping("/book/{bookId}/availability")

    public AvailabilityRatioDTO getAvailabilityRatio(@PathVariable Long bookId) {

        return statisticsService.getAvailabilityRatio(bookId);

    }


    @GetMapping("/top-reviewers")

    public List<TopReviewerDTO> getTopReviewers(

            @RequestParam(defaultValue = "10") int limit

    ) {

        return statisticsService.getTopReviewers(limit);

    }


    @GetMapping("/author/{authorId}/rating")

    public AuthorStatisticsDTO getAuthorRating(@PathVariable Long authorId) {

        return statisticsService.getAuthorStatistics(authorId);

    }
}