package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.model.CopyStatus;
import agh.oot.librarby.book.service.ExactBookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book-copies")
public class ExactBookCopyController {

    private final ExactBookCopyService exactBookCopyService;

    public ExactBookCopyController(ExactBookCopyService exactBookCopyService) {
        this.exactBookCopyService = exactBookCopyService;
    }

    @PostMapping
    public ResponseEntity<ExactBookCopyResponse> createExactBookCopy(
            @RequestBody @Valid CreateExactBookCopyRequest request) {
        ExactBookCopyResponse response = exactBookCopyService.createExactBookCopy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExactBookCopyResponse> getExactBookCopyById(@PathVariable Long id) {
        ExactBookCopyResponse response = exactBookCopyService.getExactBookCopyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExactBookCopyResponse>> getAllExactBookCopies(
            @RequestParam(required = false) Long bookEditionId,
            @RequestParam(required = false) Boolean availableOnly) {
        List<ExactBookCopyResponse> copies;
        if (Boolean.TRUE.equals(availableOnly)) {
            copies = exactBookCopyService.getAvailableCopies();
        } else if (bookEditionId != null) {
            copies = exactBookCopyService.getExactBookCopiesByEditionId(bookEditionId);
        } else {
            copies = exactBookCopyService.getAllExactBookCopies();
        }
        return ResponseEntity.ok(copies);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ExactBookCopyResponse> updateCopyStatus(
            @PathVariable Long id,
            @RequestParam CopyStatus status) {
        ExactBookCopyResponse response = exactBookCopyService.updateCopyStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExactBookCopy(@PathVariable Long id) {
        exactBookCopyService.deleteExactBookCopy(id);
        return ResponseEntity.noContent().build();
    }
}
