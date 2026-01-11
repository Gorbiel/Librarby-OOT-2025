package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.model.Publisher;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.publisher.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.Locale;
import java.util.Objects;

@Service
public class BookEditionService {
    private final BookEditionRepository bookEditionRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    public BookEditionService(BookEditionRepository bookEditionRepository, PublisherRepository publisherRepository,
                              BookRepository bookRepository) {
        this.bookEditionRepository = bookEditionRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    public BookEditionResponse getBookEdition(long bookEditionId) {
        BookEdition bookEdition = bookEditionRepository.findById(bookEditionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookEdition not found"));

        return mapToResponse(bookEdition);
    }

    @Transactional
    public BookEditionResponse updateBookEditionById(long bookEditionId, UpdateBookEditionRequest request) {
        BookEdition bookEdition = bookEditionRepository.findById(bookEditionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookEdition not found"));

        if (request.isbn() != null && !Objects.equals(request.isbn(), bookEdition.getIsbn())) {
            if (bookEditionRepository.existsByIsbn(request.isbn())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN already assigned to another edition");
            }
            if (!isValidIsbnFormat(request.isbn())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ISBN format");
            }
            bookEdition.setIsbn(request.isbn());
        }

        if (request.publisherId() != null && !Objects.equals(request.publisherId(), bookEdition.getPublisher().getId())) {
            Publisher newPublisher = publisherRepository.findById(request.publisherId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
            bookEdition.setPublisher(newPublisher);
        }

        if (request.pageCount() != null) {
            bookEdition.setPageCount(request.pageCount());
        }
        if (request.publicationYear() != null) {
            bookEdition.setPublicationYear(request.publicationYear());
        }
        if (request.language() != null) {
            bookEdition.setLanguage(request.language());
        }

        return mapToResponse(bookEdition);
    }

    private BookEditionResponse mapToResponse(BookEdition bookEdition) {
        return new BookEditionResponse(
                bookEdition.getId(),
                bookEdition.getIsbn(),
                new BookBriefResponse(bookEdition.getBook().getId(), bookEdition.getBook().getTitle()),
                bookEdition.getPageCount(),
                bookEdition.getPublicationYear(),
                new PublisherResponse(bookEdition.getPublisher().getId(), bookEdition.getPublisher().getName()),
                bookEdition.getLanguage()
        );
    }

    private boolean isValidIsbnFormat(ISBN isbn) {
        return isbn != null;
    }

    @Transactional
    public BookEditionResponse createBookEdition(Long bookId, CreateBookEditionRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

        ISBN isbn = new ISBN(request.isbn());
        if (bookEditionRepository.existsByIsbn(isbn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN already exists");
        }

        Locale language;
        try {
            language = Locale.forLanguageTag(request.language());
            if (language.getLanguage().isEmpty()) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid language format");
        }

        BookEdition newEdition = new BookEdition();
        newEdition.setBook(book);
        newEdition.setPublisher(publisher);
        newEdition.setIsbn(isbn);
        newEdition.setPageCount(request.pageCount());
        newEdition.setPublicationYear(Year.of(request.publicationYear()));
        newEdition.setLanguage(language);

        BookEdition savedEdition = bookEditionRepository.save(newEdition);

        return mapToResponse(savedEdition);
    }

    public void deleteBookEdition(long bookEditionId) {
        if (!bookEditionRepository.existsById(bookEditionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BookEdition not found");
        }
        bookEditionRepository.deleteById(bookEditionId);
    }
}