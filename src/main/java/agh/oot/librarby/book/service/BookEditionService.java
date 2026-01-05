package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import agh.oot.librarby.book.model.Publisher;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.book.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Locale;

@Service
public class BookEditionService {

    private final BookEditionRepository bookEditionRepository;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public BookEditionService(BookEditionRepository bookEditionRepository,
                              BookRepository bookRepository,
                              PublisherRepository publisherRepository) {
        this.bookEditionRepository = bookEditionRepository;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    public BookEditionResponse createBookEdition(CreateBookEditionRequest request) {
        ISBN isbn = new ISBN(request.isbn());

        if (bookEditionRepository.existsByIsbn(isbn)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book edition with this ISBN already exists");
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book not found with ID: " + request.bookId()));

        Publisher publisher = null;
        if (request.publisherId() != null) {
            publisher = publisherRepository.findById(request.publisherId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Publisher not found with ID: " + request.publisherId()));
        }

        Year publicationYear = request.publicationYear() != null
                ? Year.of(request.publicationYear())
                : null;

        Locale language = Locale.forLanguageTag(request.language());

        BookEdition edition = new BookEdition(
                isbn,
                request.pageCount(),
                publicationYear,
                publisher,
                language
        );
        edition.setBook(book);

        BookEdition saved = bookEditionRepository.save(edition);
        return toResponse(saved);
    }

    public BookEditionResponse getBookEditionById(Long id) {
        BookEdition edition = bookEditionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book edition not found"));
        return toResponse(edition);
    }

    public BookEditionResponse getBookEditionByIsbn(String isbnValue) {
        ISBN isbn = new ISBN(isbnValue);
        BookEdition edition = bookEditionRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book edition not found"));
        return toResponse(edition);
    }

    public List<BookEditionResponse> getAllBookEditions() {
        return bookEditionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookEditionResponse> getBookEditionsByBookId(Long bookId) {
        return bookEditionRepository.findByBookId(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBookEdition(Long id) {
        BookEdition edition = bookEditionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book edition not found"));
        bookEditionRepository.delete(edition);
    }

    private BookEditionResponse toResponse(BookEdition edition) {
        return new BookEditionResponse(
                edition.getId(),
                edition.getIsbn().getValue(),
                edition.getBook().getId(),
                edition.getBook().getTitle(),
                edition.getPageCount(),
                edition.getPublicationYear() != null ? edition.getPublicationYear().getValue() : null,
                edition.getPublisher() != null ? edition.getPublisher().getName() : null,
                edition.getLanguage() != null ? edition.getLanguage().toLanguageTag() : null
        );
    }
}
