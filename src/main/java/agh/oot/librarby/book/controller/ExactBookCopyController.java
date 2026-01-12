package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.service.ExactBookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/books/editions")
public class ExactBookCopyController {

    private final ExactBookCopyService exactBookCopyService;

    public ExactBookCopyController(ExactBookCopyService exactBookCopyService) {
        this.exactBookCopyService = exactBookCopyService;
    }


    @PostMapping(value = "/exact-copy")
    public ResponseEntity<ExactBookCopyResponse> createExactBookCopy(
            @RequestBody @Valid CreateExactBookCopyRequest request) {
        ExactBookCopyResponse createdCopy = exactBookCopyService.createExactBookCopy(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/exact-book-copies/{id}")
                .buildAndExpand(createdCopy.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCopy);
    }

}
