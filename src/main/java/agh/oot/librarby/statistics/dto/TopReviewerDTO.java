package agh.oot.librarby.statistics.dto;

public record TopReviewerDTO(
        Long readerId,
        String firstName,
        String lastName,
        Long reviewCount
) {}
