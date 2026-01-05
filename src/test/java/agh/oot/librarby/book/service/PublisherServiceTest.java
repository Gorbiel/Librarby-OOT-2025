package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.CreatePublisherRequest;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.model.Publisher;
import agh.oot.librarby.book.repository.PublisherRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void createPublisher_ShouldSaveAndReturnResponse_WhenNameIsUnique() {
        // Given
        String name = "Wydawnictwo Literackie";
        CreatePublisherRequest request = new CreatePublisherRequest(name);

        when(publisherRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(i -> i.getArgument(0));

        // When
        PublisherResponse response = publisherService.createPublisher(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(name);

        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void createPublisher_ShouldThrowException_WhenPublisherAlreadyExists() {
        // Given
        String name = "Helion";
        CreatePublisherRequest request = new CreatePublisherRequest(name);

        when(publisherRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.of(new Publisher(name)));

        // When & Then
        assertThatThrownBy(() -> publisherService.createPublisher(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(publisherRepository, never()).save(any());
    }
}