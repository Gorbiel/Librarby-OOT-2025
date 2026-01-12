package agh.oot.librarby.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(description = "Wrapper response containing a list of book editions")
public record MultipleBookEditionResponse(
        @Schema(description = "List of book editions")
        List<BookEditionResponse> bookEditions
) {
}
