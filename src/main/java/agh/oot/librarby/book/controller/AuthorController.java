package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.dto.CreateAuthorRequest;
import agh.oot.librarby.book.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@RequestBody @Valid CreateAuthorRequest request) {
        AuthorResponse response = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        AuthorResponse response = authorService.getAuthorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors(
            @RequestParam(required = false) String lastName) {
        List<AuthorResponse> authors;
        if (lastName != null && !lastName.isBlank()) {
            authors = authorService.searchAuthorsByLastName(lastName);
        } else {
            authors = authorService.getAllAuthors();
        }
        return ResponseEntity.ok(authors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
