package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.service.BookEditionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book-editions")
public class BookEditionController {

    private final BookEditionService bookEditionService;

    public BookEditionController(BookEditionService bookEditionService) {
        this.bookEditionService = bookEditionService;
    }

    @PostMapping
    public ResponseEntity<BookEditionResponse> createBookEdition(
            @RequestBody @Valid CreateBookEditionRequest request) {
        BookEditionResponse response = bookEditionService.createBookEdition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookEditionResponse> getBookEditionById(@PathVariable Long id) {
        BookEditionResponse response = bookEditionService.getBookEditionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookEditionResponse> getBookEditionByIsbn(@PathVariable String isbn) {
        BookEditionResponse response = bookEditionService.getBookEditionByIsbn(isbn);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookEditionResponse>> getAllBookEditions(
            @RequestParam(required = false) Long bookId) {
        List<BookEditionResponse> editions;
        if (bookId != null) {
            editions = bookEditionService.getBookEditionsByBookId(bookId);
        } else {
            editions = bookEditionService.getAllBookEditions();
        }
        return ResponseEntity.ok(editions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookEdition(@PathVariable Long id) {
        bookEditionService.deleteBookEdition(id);
        return ResponseEntity.noContent().build();
    }
}
