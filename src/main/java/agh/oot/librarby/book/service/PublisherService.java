package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.CreatePublisherRequest;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.model.Publisher;
import agh.oot.librarby.book.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    public PublisherResponse createPublisher(CreatePublisherRequest request) {
        Publisher publisher = new Publisher(request.name());
        Publisher saved = publisherRepository.save(publisher);
        return toResponse(saved);
    }

    public PublisherResponse getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        return toResponse(publisher);
    }

    public List<PublisherResponse> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        publisherRepository.delete(publisher);
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}
