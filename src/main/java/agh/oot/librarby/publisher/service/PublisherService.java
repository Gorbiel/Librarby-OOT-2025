package agh.oot.librarby.publisher.service;

import agh.oot.librarby.publisher.dto.MultiplePublishersResponse;
import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;

public interface PublisherService {
    MultiplePublishersResponse getAllPublishers(String q);

    PublisherResponse createPublisher(PublisherCreateRequest request);
}
