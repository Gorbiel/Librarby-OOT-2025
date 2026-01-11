package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Genre;
import agh.oot.librarby.exception.ApiErrorResponse;
import agh.oot.librarby.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Books", description = "Endpoints for managing and browsing books")
@RestController
@RequestMapping(
        path = "/api/v1/books",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "List books", description = "Returns books. Optional query params allow basic filtering.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Books retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MultipleBooksResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameter(s)",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<MultipleBooksResponse> getBooks(
            @Parameter(description = "Filter by title substring (case-insensitive)", example = "gatsby")
            @RequestParam(required = false) String title,

            @Parameter(description = "Filter by genre", example = "FICTION")
            @RequestParam(required = false) Genre genre,

            @Parameter(description = "Filter by author ID", example = "5")
            @RequestParam(required = false) Long authorId,

            @Parameter(description = "Filter by age rating", example = "ADULT")
            @RequestParam(required = false) AgeRating ageRating
    ) {
        MultipleBooksResponse body = bookService.getBooks(new BookQueryParams(title, authorId, genre, ageRating));
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get book by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "Book ID", example = "10", required = true)
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @Operation(summary = "List age ratings", description = "Returns all possible age ratings.")
    @ApiResponse(
            responseCode = "200",
            description = "Age ratings retrieved successfully",
            content = @Content(schema = @Schema(implementation = AgeRating.class))
    )
    @GetMapping("/age-ratings")
    public ResponseEntity<List<AgeRating>> getAgeRatings() {
        List<AgeRating> body = Arrays.stream(AgeRating.values())
                .sorted(Comparator.comparingInt(AgeRating::getMinimalAge))
                .toList();
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Create a book", description = "Creates a book with optional authors and genres.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author(s) not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = BookCreateRequest.class))
            )
            @RequestBody @Valid BookCreateRequest request
    ) {
        BookResponse created = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update book", description = "Partially updates book fields such as title, genres, age rating, or authors.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long bookId,
            @RequestBody @Valid BookUpdateRequest request
    ) {
        return ResponseEntity.ok(bookService.updateBook(bookId, request));
    }

    @Operation(summary = "Add author to book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author linked successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book or author not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Author already linked to book",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/{bookId}/authors/{authorId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.addAuthor(bookId, authorId));
    }

    @Operation(summary = "Remove author from book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author unlinked successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found or author not linked",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Author is not assigned to this book",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{bookId}/authors/{authorId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> removeAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.removeAuthor(bookId, authorId));
    }

    @Operation(summary = "Add genre to book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre added successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid genre",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre already assigned to book",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/{bookId}/genres/{genre}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> addGenre(@PathVariable Long bookId, @PathVariable Genre genre) {
        return ResponseEntity.ok(bookService.addGenre(bookId, genre));
    }

    @Operation(summary = "Remove genre from book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre removed successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid genre",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre is not assigned to this book",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{bookId}/genres/{genre}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> removeGenre(@PathVariable Long bookId, @PathVariable Genre genre) {
        return ResponseEntity.ok(bookService.removeGenre(bookId, genre));
    }

    // THIS IS TECHNICALLY A REDUNDANT ENDPOINT SINCE AGE RATING CAN BE UPDATED VIA PATCH /{bookId}
    @Operation(summary = "Set age rating", description = "Overwrites the book age rating.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Age rating updated",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PutMapping(value = "/{bookId}/age-rating", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<BookResponse> setAgeRating(
            @PathVariable Long bookId,
            @RequestBody @Valid AgeRatingUpdateRequest request
    ) {
        return ResponseEntity.ok(bookService.setAgeRating(bookId, request));
    }

    @Operation(summary = "Delete book", description = "Deletes a book ONLY if it is not referenced by other records.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Book is referenced and cannot be deleted",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
