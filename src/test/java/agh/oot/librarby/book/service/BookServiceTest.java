package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.BookResponse;
import agh.oot.librarby.book.dto.CreateBookRequest;
import agh.oot.librarby.book.model.AgeRating;
import agh.oot.librarby.book.model.Author;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.Genre;
import agh.oot.librarby.book.repository.AuthorRepository;
import agh.oot.librarby.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static agh.oot.librarby.util.TestEntityUtils.setId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService underTest;

    @Test
    void createBook_success_savesAndReturnsBook() {
        // given
        Author author = new Author("George", "Orwell");
        setId(author, 1L);

        CreateBookRequest request = new CreateBookRequest(
                "1984",
                Set.of(Genre.DYSTOPIAN, Genre.SCIENCE_FICTION),
                AgeRating.TEENAGER,
                Set.of(1L)
        );

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            setId(b, 1L);
            return b;
        });

        // when
        BookResponse response = underTest.createBook(request);

        // then
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(captor.capture());
        Book captured = captor.getValue();
        assertEquals("1984", captured.getTitle());
        assertEquals(1, captured.getAuthors().size());
        assertNotNull(response);
        assertEquals("1984", response.title());
    }

    @Test
    void createBook_authorNotFound_throws() {
        // given
        CreateBookRequest request = new CreateBookRequest(
                "1984",
                Set.of(Genre.DYSTOPIAN),
                AgeRating.TEENAGER,
                Set.of(99L)
        );

        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.createBook(request));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookById_notFound_throws() {
        // given
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.getBookById(99L));
    }
}
