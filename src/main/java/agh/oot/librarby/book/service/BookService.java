package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.dto.BookResponse;
import agh.oot.librarby.book.dto.CreateBookRequest;
import agh.oot.librarby.book.model.Author;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.repository.AuthorRepository;
import agh.oot.librarby.book.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Set<Author> authors = new HashSet<>();
        for (Long authorId : request.authorIds()) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Author not found with ID: " + authorId));
            authors.add(author);
        }

        Book book = new Book(
                request.title(),
                request.genres(),
                request.ageRating(),
                authors
        );

        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return toResponse(book);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookResponse> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        bookRepository.delete(book);
    }

    private BookResponse toResponse(Book book) {
        Set<AuthorResponse> authorResponses = book.getAuthors().stream()
                .map(author -> new AuthorResponse(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName()
                ))
                .collect(Collectors.toSet());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getGenres(),
                book.getAgeRating(),
                authorResponses
        );
    }
}
