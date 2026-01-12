package agh.oot.librarby.publisher.mapper;

import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.model.Publisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PublisherResponseMapper {
    public PublisherResponse toDto(Publisher publisher) {
        Objects.requireNonNull(publisher, "publisher must not be null");
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName()
        );
    }
}
