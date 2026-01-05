package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.dto.CreateAuthorRequest;
import agh.oot.librarby.book.model.Author;
import agh.oot.librarby.book.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        Author author = new Author(request.firstName(), request.lastName());
        Author saved = authorRepository.save(author);
        return toResponse(saved);
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        return toResponse(author);
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuthorResponse> searchAuthorsByLastName(String lastName) {
        return authorRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        authorRepository.delete(author);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}
