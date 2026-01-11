package agh.oot.librarby.publisher.service;

import agh.oot.librarby.publisher.dto.MultiplePublishersResponse;
import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.mapper.MultiplePublishersResponseMapper;
import agh.oot.librarby.publisher.mapper.PublisherResponseMapper;
import agh.oot.librarby.publisher.model.Publisher;
import agh.oot.librarby.publisher.repository.PublisherRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherResponseMapper publisherResponseMapper;
    private final MultiplePublishersResponseMapper multiplePublishersResponseMapper;

    public PublisherServiceImpl(PublisherRepository publisherRepository, PublisherResponseMapper publisherResponseMapper, MultiplePublishersResponseMapper multiplePublishersResponseMapper) {
        this.publisherRepository = publisherRepository;
        this.publisherResponseMapper = publisherResponseMapper;
        this.multiplePublishersResponseMapper = multiplePublishersResponseMapper;
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

        return publisherResponseMapper.toDto(saved);
    }

    public MultiplePublishersResponse getAllPublishers(String q) {
        List<Publisher> publishers =
                (q == null || q.isBlank())
                        ? publisherRepository.findAll()
                        : publisherRepository.findByNameContainingIgnoreCase(q.trim());

        return multiplePublishersResponseMapper.toDto(publishers);
    }
}