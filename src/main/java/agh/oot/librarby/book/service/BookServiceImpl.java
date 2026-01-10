package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.mapper.BookResponseMapper;
import agh.oot.librarby.book.model.*;
import agh.oot.librarby.book.repository.AuthorRepository;
import agh.oot.librarby.book.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookResponseMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           BookResponseMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookResponse createBook(BookCreateRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        Set<Author> authors = resolveAuthors(request.authorIds());
        Set<Genre> genres = request.genres() != null ? new HashSet<>(request.genres()) : new HashSet<>();

        Book book = new Book(request.title(), genres, request.ageRating(), authors);

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public MultipleBooksResponse getBooks(BookQueryParams params) {
        // If you don’t want a params DTO, you can accept nullable params directly in controller.
        List<Book> books;

        if (params == null) {
            books = bookRepository.findAll();
        } else if (params.authorId() != null) {
            books = bookRepository.findByAuthors_Id(params.authorId());
        } else if (params.genre() != null) {
            books = bookRepository.findByGenres(params.genre());
        } else if (params.ageRating() != null) {
            books = bookRepository.findByAgeRating(params.ageRating());
        } else if (params.title() != null && !params.title().isBlank()) {
            books = bookRepository.findByTitleContainingIgnoreCase(params.title());
        } else {
            books = bookRepository.findAll();
        }

        List<BookResponse> dtos = books.stream().map(bookMapper::toDto).toList();
        return new MultipleBooksResponse(dtos);
    }

    @Override
    public BookResponse updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        Optional.ofNullable(request.title()).ifPresent(book::setTitle);
        Optional.ofNullable(request.ageRating()).ifPresent(book::setAgeRating);

        if (request.genres() != null) {
            book.setGenres(request.genres());
        }

        // Optional: allow updating authorIds in PATCH
        if (request.authorIds() != null) {
            Set<Author> authors = resolveAuthors(request.authorIds());
            book.setAuthors(authors);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    public BookResponse addAuthor(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        // Set => idempotent
        Set<Author> authors = book.getAuthors();
        authors.add(author);
        book.setAuthors(authors);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponse removeAuthor(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        boolean removed = book.getAuthors().removeIf(a -> Objects.equals(a.getId(), authorId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author is not assigned to this book");
        }

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponse addGenre(Long bookId, String genreRaw) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        Genre genre = parseGenre(genreRaw);
        Set<Genre> genres = book.getGenres();
        genres.add(genre);
        book.setGenres(genres);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponse removeGenre(Long bookId, String genreRaw) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        Genre genre = parseGenre(genreRaw);
        Set<Genre> genres = book.getGenres();

        if (!genres.remove(genre)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre is not assigned to this book");
        }

        book.setGenres(genres);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponse setAgeRating(Long bookId, AgeRatingUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (request == null || request.ageRating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age rating must not be null");
        }

        book.setAgeRating(request.ageRating());
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        try {
            bookRepository.delete(book);
        } catch (DataIntegrityViolationException ex) {
            // This is your “cannot delete if referenced” rule.
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book cannot be deleted because it is referenced by other records"
            );
        }
    }

    private Set<Author> resolveAuthors(Set<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) return new HashSet<>();

        List<Author> authors = authorRepository.findAllById(authorIds);
        if (authors.size() != authorIds.size()) {
            Set<Long> found = authors.stream().map(Author::getId).collect(Collectors.toSet());
            Set<Long> missing = authorIds.stream().filter(id -> !found.contains(id)).collect(Collectors.toSet());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author(s) not found: " + missing);
        }
        return new HashSet<>(authors);
    }

    private Genre parseGenre(String raw) {
        try {
            return Genre.valueOf(raw);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid genre: '" + raw + "'");
        }
    }
}
