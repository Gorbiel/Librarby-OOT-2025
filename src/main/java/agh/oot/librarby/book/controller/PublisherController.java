package agh.oot.librarby.book.controller;

import agh.oot.librarby.book.dto.CreatePublisherRequest;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(@RequestBody @Valid CreatePublisherRequest request) {
        PublisherResponse response = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisherById(@PathVariable Long id) {
        PublisherResponse response = publisherService.getPublisherById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PublisherResponse>> getAllPublishers() {
        List<PublisherResponse> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
