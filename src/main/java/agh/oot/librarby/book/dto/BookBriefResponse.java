package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookBriefResponse(
        @Schema(description = "Book (title-level) ID", example = "10")
        Long id,

        @Schema(description = "Book title", example = "The Great Gatsby")
        String title
) {}
