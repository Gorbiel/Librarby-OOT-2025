package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.dto.UpdateExactBookCopyRequest;
import agh.oot.librarby.book.service.ExactBookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Book Copies", description = "Endpoints for managing book copies")
@RequestMapping(path = "/api/v1/books/exact-book", produces = "application/json")
public class ExactBookCopyController {

    private final ExactBookCopyService exactBookCopyService;

    public ExactBookCopyController(ExactBookCopyService exactBookCopyService) {
        this.exactBookCopyService = exactBookCopyService;
    }

    @Operation(summary = "Create Exact Book Copy", description = "Creates a new exact book copy")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Exact book copy created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced book not found"
            )
    })
    @PostMapping(value = "/create-book")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<ExactBookCopyResponse> createExactBookCopy(
            @RequestBody @Valid CreateExactBookCopyRequest request) {
        ExactBookCopyResponse createdCopy = exactBookCopyService.createExactBookCopy(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/exact-book-copies/{id}")
                .buildAndExpand(createdCopy.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCopy);
    }

    @Operation(summary = "Get Exact Book Copy", description = "Retrieves an exact book copy by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Exact book copy retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exact book copy not found"
            )
    })
    @GetMapping(value = "/{bookId}")
    public ResponseEntity<ExactBookCopyResponse> getExactBookCopy(@PathVariable Long bookId) {
        ExactBookCopyResponse body =  exactBookCopyService.getExactBookCopy(bookId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(summary = "Delete Exact Book Copy", description = "Deletes an exact book copy by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Exact book copy deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exact book copy not found"
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @DeleteMapping(value = "/{bookId}")
    public ResponseEntity<Void> deleteExactBookCopy(@PathVariable Long bookId) {
        exactBookCopyService.deleteExactBookCopy(bookId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Exact Book Copy", description = "Updates an existing exact book copy by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Exact book copy updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exact book copy not found"
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<ExactBookCopyResponse> updateExactBookCopy(
            @PathVariable Long id,
            @RequestBody @Valid UpdateExactBookCopyRequest request) {

        ExactBookCopyResponse response = exactBookCopyService.updateExactBookCopy(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
