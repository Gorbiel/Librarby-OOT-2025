package agh.oot.librarby.publisher.service;

import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.mapper.PublisherResponseMapper;
import agh.oot.librarby.publisher.model.Publisher;
import agh.oot.librarby.publisher.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherResponseMapper publisherResponseMapper;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void createPublisher_ShouldSaveAndReturnResponse_WhenNameIsUnique() {
        // Given
        String name = "Wydawnictwo Literackie";
        PublisherCreateRequest request = new PublisherCreateRequest(name);

        when(publisherRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(i -> i.getArgument(0));
        when(publisherResponseMapper.toDto(any(Publisher.class)))
                .thenAnswer(i -> {
                    Publisher p = i.getArgument(0);
                    return new PublisherResponse(p.getId(), p.getName());
                });

        // When
        PublisherResponse response = publisherService.createPublisher(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(name);

        verify(publisherRepository).save(any(Publisher.class));
        verify(publisherResponseMapper).toDto(any(Publisher.class));
    }

    @Test
    void createPublisher_ShouldThrowException_WhenPublisherAlreadyExists() {
        // Given
        String name = "Helion";
        PublisherCreateRequest request = new PublisherCreateRequest(name);

        when(publisherRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.of(new Publisher(name)));

        // When & Then
        assertThatThrownBy(() -> publisherService.createPublisher(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(publisherRepository, never()).save(any());
        verifyNoInteractions(publisherResponseMapper);
    }
}
