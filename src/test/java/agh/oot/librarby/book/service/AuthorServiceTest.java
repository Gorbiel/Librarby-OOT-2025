package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.dto.CreateAuthorRequest;
import agh.oot.librarby.book.model.Author;
import agh.oot.librarby.book.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static agh.oot.librarby.util.TestEntityUtils.setId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService underTest;

    @Test
    void createAuthor_success_savesAndReturnsAuthor() {
        // given
        CreateAuthorRequest request = new CreateAuthorRequest("George", "Orwell");

        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> {
            Author a = invocation.getArgument(0);
            setId(a, 1L);
            return a;
        });

        // when
        AuthorResponse response = underTest.createAuthor(request);

        // then
        ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository, times(1)).save(captor.capture());
        Author captured = captor.getValue();
        assertEquals("George", captured.getFirstName());
        assertEquals("Orwell", captured.getLastName());
        assertNotNull(response);
        assertEquals("George", response.firstName());
        assertEquals("Orwell", response.lastName());
    }

    @Test
    void getAuthorById_notFound_throws() {
        // given
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.getAuthorById(99L));
    }

    @Test
    void getAllAuthors_returnsList() {
        // given
        Author author1 = new Author("George", "Orwell");
        Author author2 = new Author("Jane", "Austen");
        when(authorRepository.findAll()).thenReturn(List.of(author1, author2));

        // when
        List<AuthorResponse> result = underTest.getAllAuthors();

        // then
        assertEquals(2, result.size());
    }

    @Test
    void searchAuthorsByLastName_filtersCorrectly() {
        // given
        Author author = new Author("Jane", "Austen");
        when(authorRepository.findByLastNameContainingIgnoreCase("Austen")).thenReturn(List.of(author));

        // when
        List<AuthorResponse> result = underTest.searchAuthorsByLastName("Austen");

        // then
        assertEquals(1, result.size());
        assertEquals("Jane", result.get(0).firstName());
        assertEquals("Austen", result.get(0).lastName());
    }

    @Test
    void deleteAuthor_notFound_throws() {
        // given
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.deleteAuthor(99L));
        verify(authorRepository, never()).delete(any());
    }
}
