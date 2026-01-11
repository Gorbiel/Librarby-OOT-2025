package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.AuthorCreateRequest;
import agh.oot.librarby.author.model.Author;
import agh.oot.librarby.author.repository.AuthorRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

//    @Mock
//    private AuthorRepository authorRepository;
//
//    @InjectMocks
//    private AuthorService authorService;
//
//    @Test
//    void createAuthor_ShouldSaveAndReturnResponse_WhenAuthorIsUnique() {
//        // Given
//        String firstName = "Stanisław";
//        String lastName = "Lem";
//        int birthYear = 1921;
//        AuthorCreateRequest request = new AuthorCreateRequest(firstName, lastName, birthYear);
//
//        when(authorRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthYear(
//                firstName, lastName, Year.of(birthYear)))
//                .thenReturn(Optional.empty());
//
//        when(authorRepository.save(any(Author.class))).thenAnswer(i -> i.getArgument(0));
//
//        // When
//        AuthorResponse response = authorService.createAuthor(request);
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.firstName()).isEqualTo(firstName);
//        assertThat(response.lastName()).isEqualTo(lastName);
//        assertThat(response.birthYear()).isEqualTo(Year.of(birthYear));
//
//        verify(authorRepository).save(any(Author.class));
//    }
//
//    @Test
//    void createAuthor_ShouldThrowException_WhenAuthorAlreadyExists() {
//        // Given
//        String firstName = "Andrzej";
//        String lastName = "Sapkowski";
//        int birthYear = 1948;
//        AuthorCreateRequest request = new AuthorCreateRequest(firstName, lastName, birthYear);
//
//        when(authorRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthYear(
//                firstName, lastName, Year.of(birthYear)))
//                .thenReturn(Optional.of(new Author(firstName, lastName, Year.of(birthYear))));
//
//        // When & Then
//        assertThatThrownBy(() -> authorService.createAuthor(request))
//                .isInstanceOf(ResourceAlreadyExistsException.class)
//                .hasMessageContaining("already exists");
//
//        verify(authorRepository, never()).save(any());
//    }
}