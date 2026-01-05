package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.dto.CreateAuthorRequest;
import agh.oot.librarby.book.model.Author;
import agh.oot.librarby.book.repository.AuthorRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.StringJoiner;
import java.util.StringTokenizer;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        authorRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthYear(
                request.firstName(),
                request.lastName(),
                Year.of(request.birthYear())
        ).ifPresent(author -> {
            String message = "Author %s %s (birth year %d) already exists."
                    .formatted(request.firstName(), request.lastName(), request.birthYear());

            throw new ResourceAlreadyExistsException(message, author.getId());
        });

        Author author = new Author(request.firstName(), request.lastName(), Year.of(request.birthYear()));
        Author saved = authorRepository.save(author);
        return toResponse(saved);
    }

    private AuthorResponse toResponse(Author a) {
        return new AuthorResponse(a.getId(), a.getFirstName(), a.getLastName(), a.getBirthYear());
    }
}
