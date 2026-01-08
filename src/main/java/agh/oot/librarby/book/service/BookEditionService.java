package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.BookBriefResponse;
import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.dto.UpdateBookEditionRequest;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ISBN;
import agh.oot.librarby.book.model.Publisher;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class BookEditionService {
    private final BookEditionRepository bookEditionRepository;
    private final PublisherRepository publisherRepository;

    public BookEditionService(BookEditionRepository bookEditionRepository, PublisherRepository publisherRepository) {
        this.bookEditionRepository = bookEditionRepository;
        this.publisherRepository = publisherRepository;
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
}