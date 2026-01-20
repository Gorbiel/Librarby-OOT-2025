package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.dto.UpdateBookEditionRequest;
import agh.oot.librarby.book.service.BookEditionServiceImpl;
import agh.oot.librarby.exception.ApiErrorResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book editions", description = "Endpoints for managing individual book editions")
@RestController
@RequestMapping(path = "/api/v1/book-editions")
public class BookEditionController {

    private final BookEditionServiceImpl bookEditionServiceImpl;

    public BookEditionController(BookEditionServiceImpl bookEditionServiceImpl) {
        this.bookEditionServiceImpl = bookEditionServiceImpl;
    }

    @Operation(summary = "Get book edition by ID", description = "Fetch a single book edition by its identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book edition retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookEditionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid path parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book edition not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<BookEditionResponse> getBookEdition(
            @Parameter(description = "Book edition ID", example = "15", required = true)
            @PathVariable Long id) {
        BookEditionResponse body = bookEditionServiceImpl.getBookEdition(id);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(summary = "Update book edition", description = "Partially update fields of a book edition.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book edition updated successfully",
                    content = @Content(schema = @Schema(implementation = BookEditionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book edition or referenced entity not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - update would violate uniqueness or business rules",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<BookEditionResponse> updateBookEditionById(
            @Parameter(description = "Book edition ID", example = "15", required = true)
            @PathVariable Long id,
            @RequestBody @Valid UpdateBookEditionRequest request) {
        BookEditionResponse response = bookEditionServiceImpl.updateBookEditionById(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "Delete book edition", description = "Delete a book edition if it is not referenced by other records.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Book edition deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book edition not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot delete because the edition is referenced by other records",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBookEdition(
            @Parameter(description = "Book edition ID", example = "15", required = true)
            @PathVariable Long id) {
        bookEditionServiceImpl.deleteBookEdition(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create book edition", description = "Create a new edition for an existing book.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book edition created",
                    content = @Content(schema = @Schema(implementation = BookEditionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or ISBN already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced book or publisher not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<BookEditionResponse> createBookEdition(
            @RequestBody @Valid CreateBookEditionRequest request) {

        BookEditionResponse createdEdition = bookEditionServiceImpl.createBookEdition(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/books/editions/{id}")
                .buildAndExpand(createdEdition.id())
                .toUri();

        return ResponseEntity.created(location).body(createdEdition);
    }
}
