package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.service.BookEditionService;
import agh.oot.librarby.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
