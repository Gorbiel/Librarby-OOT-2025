package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.service.ExactBookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/books/exact-book")
public class ExactBookCopyController {

    private final ExactBookCopyService exactBookCopyService;

    public ExactBookCopyController(ExactBookCopyService exactBookCopyService) {
        this.exactBookCopyService = exactBookCopyService;
    }


    @PostMapping(value = "/create-book")
    public ResponseEntity<ExactBookCopyResponse> createExactBookCopy(
            @RequestBody @Valid CreateExactBookCopyRequest request) {
        ExactBookCopyResponse createdCopy = exactBookCopyService.createExactBookCopy(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/exact-book-copies/{id}")
                .buildAndExpand(createdCopy.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCopy);
    }

    @GetMapping(value = "/{bookId}")
    public ResponseEntity<ExactBookCopyResponse> getExactBookCopy(@PathVariable Long bookId) {
        ExactBookCopyResponse body =  exactBookCopyService.getExactBookCopy(bookId);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping(value = "/{bookId}")
    public ResponseEntity<Void> deleteExactBookCopy(@PathVariable Long bookId) {
        exactBookCopyService.deleteExactBookCopy(bookId);
        return ResponseEntity.noContent().build();
    }

}
