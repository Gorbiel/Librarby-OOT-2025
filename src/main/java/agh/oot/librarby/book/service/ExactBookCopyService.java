package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.ExactBookCopy;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExactBookCopyService {

    private final BookEditionRepository bookEditionRepository;
    private final ExactBookCopyRepository exactBookCopyRepository;

    public ExactBookCopyService(BookEditionRepository bookEditionRepository, ExactBookCopyRepository exactBookCopyRepository) {
        this.bookEditionRepository = bookEditionRepository;
        this.exactBookCopyRepository = exactBookCopyRepository;
    }

    @Transactional
    public ExactBookCopyResponse createExactBookCopy(CreateExactBookCopyRequest request) {

        BookEdition bookEdition = bookEditionRepository.findById(request.bookEditionId())
                .orElseThrow(() -> new EntityNotFoundException("BookEdition not found with id: " + request.bookEditionId()));

        ExactBookCopy copy = new ExactBookCopy(bookEdition, request.status());

        ExactBookCopy savedCopy = exactBookCopyRepository.save(copy);

        return mapToResponse(savedCopy);
    }

    private ExactBookCopyResponse mapToResponse(ExactBookCopy copy) {
        BookEdition edition = copy.getBookEdition();
        // Mapowanie Book -> BookBriefResponse
        BookBriefResponse bookBrief = new BookBriefResponse(
                edition.getBook().getId(),
                edition.getBook().getTitle()
        );

        // Mapowanie Publisher -> PublisherResponse
        PublisherResponse publisherResponse = null;
        if (edition.getPublisher() != null) {
            publisherResponse = new PublisherResponse(
                    edition.getPublisher().getId(),
                    edition.getPublisher().getName()
            );
        }

        // Mapowanie BookEdition -> BookEditionResponse
        BookEditionResponse editionResponse = new BookEditionResponse(
                edition.getId(),
                edition.getIsbn(),
                bookBrief,
                edition.getPageCount(),
                edition.getPublicationYear(),
                publisherResponse,
                edition.getLanguage()
        );

        // Finalne złożenie ExactBookCopyResponse
        return new ExactBookCopyResponse(
                copy.getId(),
                editionResponse,
                copy.getStatus()
        );

    }

    @Transactional(readOnly = true)
    public ExactBookCopyResponse getExactBookCopy(Long bookId) {
        ExactBookCopy copy = exactBookCopyRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExactBookCopy not found"));

        return mapToResponse(copy);
    }

    @Transactional
    public void deleteExactBookCopy(Long bookId) {
        ExactBookCopy bookCopy = exactBookCopyRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Exact book copy with id:  " + bookId + " not found."));
        exactBookCopyRepository.delete(bookCopy);
    }
}

