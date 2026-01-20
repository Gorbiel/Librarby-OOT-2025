package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.CreateBookEditionRequest;
import agh.oot.librarby.book.dto.UpdateBookEditionRequest;
import agh.oot.librarby.book.mapper.BookEditionResponseMapper;
import agh.oot.librarby.book.model.Book;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.BookRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import agh.oot.librarby.exception.ResourceAlreadyExistsException;
import agh.oot.librarby.publisher.model.Publisher;
import agh.oot.librarby.publisher.repository.PublisherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.Locale;
import java.util.Optional;

@Service
public class BookEditionServiceImpl implements BookEditionService {
    private final BookEditionRepository bookEditionRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final ExactBookCopyRepository exactBookCopyRepository;
    private final BookEditionResponseMapper responseMapper;

    public BookEditionServiceImpl(BookEditionRepository bookEditionRepository, PublisherRepository publisherRepository,
                                  BookRepository bookRepository, ExactBookCopyRepository exactBookCopyRepository, BookEditionResponseMapper responseMapper) {
        this.bookEditionRepository = bookEditionRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
        this.exactBookCopyRepository = exactBookCopyRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional(readOnly = true)
    public BookEditionResponse getBookEdition(long bookEditionId) {
        BookEdition bookEdition = bookEditionRepository.findById(bookEditionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookEdition not found"));

        return responseMapper.toDto(bookEdition);
    }

    @Transactional
    public BookEditionResponse updateBookEditionById(Long id, UpdateBookEditionRequest request) {
        BookEdition edition = bookEditionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BookEdition " + id + "not found"));

        if (request.isbn() != null) {
            updateIsbn(edition, new ISBN(request.isbn()));
        }

        if (request.publisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.publisherId())
                    .orElseThrow(() -> new EntityNotFoundException("Publisher " + request.publisherId() + "not found"));
            edition.setPublisher(publisher);
        }

        Optional.ofNullable(request.pageCount()).ifPresent(edition::setPageCount);
        Optional.ofNullable(request.publicationYear()).ifPresent(y -> edition.setPublicationYear(Year.of(y)));

        return responseMapper.toDto(edition);
    }

    private void updateIsbn(BookEdition edition, ISBN newIsbn) {
        if (edition.getIsbn().equals(newIsbn)) return;
        if (bookEditionRepository.existsByIsbn(newIsbn)) {
            throw new ResourceAlreadyExistsException("ISBN already exists", newIsbn.getValue());
        }
        edition.setIsbn(newIsbn);
    }

    @Transactional
    public BookEditionResponse createBookEdition(CreateBookEditionRequest request) {
        Book book = bookRepository.findById(request.bookId())
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
        } catch (Exception ignored) {
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

        return responseMapper.toDto(savedEdition);
    }

    @Transactional
    public void deleteBookEdition(long id) {
        BookEdition edition = bookEditionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BookEdition " + id + " not found."));

        if (exactBookCopyRepository.existsByBookEdition(edition)) {
            throw new IllegalStateException("Book cannot be deleted because it is referenced by exact book copy.");
        }

        try {
            bookEditionRepository.delete(edition);
        } catch (DataIntegrityViolationException ignored) {
            // This is your “cannot delete if referenced” rule.
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book edition cannot be deleted because it is referenced by other records"
            );
        }
    }
}