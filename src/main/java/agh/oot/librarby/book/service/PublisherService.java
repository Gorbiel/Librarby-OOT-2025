package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.CreatePublisherRequest;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.model.Publisher;
import agh.oot.librarby.book.repository.PublisherRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
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
        publisherRepository.findByNameIgnoreCase(request.name())
                .ifPresent(p -> {
                    throw new ResourceAlreadyExistsException(
                            "Publisher with name '" + request.name() + "' already exists.",
                            p.getId());
                });

        Publisher publisher = new Publisher(request.name());
        Publisher saved = publisherRepository.save(publisher);

        return toResponse(saved);
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}
