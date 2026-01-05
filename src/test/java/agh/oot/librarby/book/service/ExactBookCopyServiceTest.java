package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.model.*;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static agh.oot.librarby.util.TestEntityUtils.setId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExactBookCopyServiceTest {

    @Mock
    private ExactBookCopyRepository exactBookCopyRepository;

    @Mock
    private BookEditionRepository bookEditionRepository;

    @InjectMocks
    private ExactBookCopyService underTest;

    @Test
    void createExactBookCopy_success_savesWithDefaultStatus() {
        // given
        Book book = new Book("1984", Set.of(Genre.DYSTOPIAN), AgeRating.TEENAGER, Set.of());
        setId(book, 1L);

        BookEdition edition = new BookEdition(
                new ISBN("978-0451524935"),
                328,
                Year.of(1961),
                null,
                Locale.ENGLISH
        );
        edition.setBook(book);
        setId(edition, 1L);

        CreateExactBookCopyRequest request = new CreateExactBookCopyRequest(1L, null);

        when(bookEditionRepository.findById(1L)).thenReturn(Optional.of(edition));
        when(exactBookCopyRepository.save(any(ExactBookCopy.class))).thenAnswer(invocation -> {
            ExactBookCopy copy = invocation.getArgument(0);
            setId(copy, 1L);
            return copy;
        });

        // when
        ExactBookCopyResponse response = underTest.createExactBookCopy(request);

        // then
        ArgumentCaptor<ExactBookCopy> captor = ArgumentCaptor.forClass(ExactBookCopy.class);
        verify(exactBookCopyRepository, times(1)).save(captor.capture());
        ExactBookCopy captured = captor.getValue();
        assertEquals(CopyStatus.AVAILABLE, captured.getStatus());
        assertNotNull(response);
        assertEquals(CopyStatus.AVAILABLE, response.status());
    }

    @Test
    void createExactBookCopy_success_savesWithProvidedStatus() {
        // given
        Book book = new Book("1984", Set.of(Genre.DYSTOPIAN), AgeRating.TEENAGER, Set.of());
        setId(book, 1L);

        BookEdition edition = new BookEdition(
                new ISBN("978-0451524935"),
                328,
                Year.of(1961),
                null,
                Locale.ENGLISH
        );
        edition.setBook(book);
        setId(edition, 1L);

        CreateExactBookCopyRequest request = new CreateExactBookCopyRequest(1L, CopyStatus.UNAVAILABLE);

        when(bookEditionRepository.findById(1L)).thenReturn(Optional.of(edition));
        when(exactBookCopyRepository.save(any(ExactBookCopy.class))).thenAnswer(invocation -> {
            ExactBookCopy copy = invocation.getArgument(0);
            setId(copy, 1L);
            return copy;
        });

        // when
        ExactBookCopyResponse response = underTest.createExactBookCopy(request);

        // then
        ArgumentCaptor<ExactBookCopy> captor = ArgumentCaptor.forClass(ExactBookCopy.class);
        verify(exactBookCopyRepository).save(captor.capture());
        assertEquals(CopyStatus.UNAVAILABLE, captor.getValue().getStatus());
    }

    @Test
    void createExactBookCopy_editionNotFound_throws() {
        // given
        CreateExactBookCopyRequest request = new CreateExactBookCopyRequest(99L, null);
        when(bookEditionRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.createExactBookCopy(request));
        verify(exactBookCopyRepository, never()).save(any());
    }

    @Test
    void updateCopyStatus_notFound_throws() {
        // given
        when(exactBookCopyRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> underTest.updateCopyStatus(99L, CopyStatus.BORROWED));
    }
}
