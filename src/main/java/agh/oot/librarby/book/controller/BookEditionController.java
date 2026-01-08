package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.dto.UpdateBookEditionRequest;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.service.BookEditionService;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/books/editions")
public class BookEditionController {

    private final BookEditionService bookEditionService;

    public BookEditionController(BookEditionService bookEditionService) {
        this.bookEditionService = bookEditionService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BookEditionResponse> getBookEdition(@PathVariable Long id) {
        BookEditionResponse body = bookEditionService.getBookEdition(id);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<BookEditionResponse> updateBookEditionById(@PathVariable Long id,
                                                                     @RequestBody @Valid UpdateBookEditionRequest request) {
        BookEditionResponse response = bookEditionService.updateBookEditionById(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/{bookId}")
    public ResponseEntity<BookEditionResponse> createBookEdition(
            @PathVariable Long bookId,
            @RequestBody @Valid CreateBookEditionRequest request) {

        BookEditionResponse createdEdition = bookEditionService.createBookEdition(bookId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/books/editions/{id}")
                .buildAndExpand(createdEdition.id())
                .toUri();

        return ResponseEntity.created(location).body(createdEdition);
    }
 }
