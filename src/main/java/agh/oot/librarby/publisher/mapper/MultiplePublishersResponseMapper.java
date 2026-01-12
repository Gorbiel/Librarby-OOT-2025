package agh.oot.librarby.publisher.mapper;

import agh.oot.librarby.publisher.dto.MultiplePublishersResponse;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.model.Publisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MultiplePublishersResponseMapper {
    private final PublisherResponseMapper publisherMapper;

    public MultiplePublishersResponseMapper(PublisherResponseMapper publisherMapper) {
        this.publisherMapper = publisherMapper;
    }

    public MultiplePublishersResponse toDto(List<Publisher> publishers) {
        Objects.requireNonNull(publishers, "publishers must not be null");

        List<PublisherResponse> items = publishers.stream()
                .map(publisherMapper::toDto)
                .toList();

        return new MultiplePublishersResponse(items);
    }
}
