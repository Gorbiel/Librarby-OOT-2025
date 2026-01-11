package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.AuthorCreateRequest;
import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.author.mapper.AuthorResponseMapper;
import agh.oot.librarby.author.mapper.MultipleAuthorsResponseMapper;
import agh.oot.librarby.author.model.Author;
import agh.oot.librarby.author.repository.AuthorRepository;
import agh.oot.librarby.book.dto.BookResponse;
import agh.oot.librarby.book.dto.MultipleBooksResponse;
import agh.oot.librarby.book.mapper.MultipleBooksResponseMapper;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorResponseMapper authorResponseMapper;
    private final MultipleAuthorsResponseMapper multipleAuthorsResponseMapper;
    private final BookRepository bookRepository;
    private final MultipleBooksResponseMapper multipleBooksMapper;

    public AuthorServiceImpl(
            AuthorRepository authorRepository,
            AuthorResponseMapper authorResponseMapper,
            MultipleAuthorsResponseMapper multipleAuthorsResponseMapper,
            BookRepository bookRepository,
            MultipleBooksResponseMapper multipleBooksMapper
    ) {
        this.authorRepository = authorRepository;
        this.authorResponseMapper = authorResponseMapper;
        this.multipleAuthorsResponseMapper = multipleAuthorsResponseMapper;
        this.bookRepository = bookRepository;
        this.multipleBooksMapper = multipleBooksMapper;
    }

    @Override
    public MultipleAuthorsResponse listAuthors(String q) {
        List<Author> authors;

        if (q == null || q.isBlank()) {
            authors = authorRepository.findAll();
        } else {
            String query = q.trim();
            authors = authorRepository.searchByName(query);
        }

        return multipleAuthorsResponseMapper.toDto(authors);
    }

    @Override
    public AuthorResponse getAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found")
                );

        return authorResponseMapper.toDto(author);
    }

    // TODO: add more filtering options (e.g., by genre, age rating)
    @Override
    @Transactional(readOnly = true)
    public MultipleBooksResponse getBooksByAuthorId(Long authorId) {
        boolean exists = authorRepository.existsById(authorId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }

        List<Book> books = bookRepository.findByFiltered(null, null, authorId, null);
        return multipleBooksMapper.toDto(books);
    }

    @Override
    @Transactional
    public AuthorResponse createAuthor(AuthorCreateRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // @Valid should handle blank firstName, but keeping this defensive check is fine:
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName must not be blank");
        }

        Author author = new Author(
                request.firstName().trim(),
                request.middleName() == null ? null : request.middleName().trim(),
                request.lastName() == null ? null : request.lastName().trim()
        );

        Author saved = authorRepository.save(author);
        return authorResponseMapper.toDto(saved);
    }
}
