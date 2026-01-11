package agh.oot.librarby.publisher.service;

import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.model.Publisher;
import agh.oot.librarby.publisher.repository.PublisherRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherCreateRequest request) {
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
