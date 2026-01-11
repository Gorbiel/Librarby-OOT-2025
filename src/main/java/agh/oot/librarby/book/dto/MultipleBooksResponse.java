package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Wrapper response containing a list of books")
public record MultipleBooksResponse(
        @Schema(description = "List of books")
        List<BookResponse> books
) {}
