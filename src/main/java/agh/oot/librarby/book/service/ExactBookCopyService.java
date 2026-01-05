package agh.oot.librarby.book.service;

import agh.oot.librarby.book.dto.CreateExactBookCopyRequest;
import agh.oot.librarby.book.dto.ExactBookCopyResponse;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.model.CopyStatus;
import agh.oot.librarby.book.model.ExactBookCopy;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.book.repository.ExactBookCopyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExactBookCopyService {

    private final ExactBookCopyRepository exactBookCopyRepository;
    private final BookEditionRepository bookEditionRepository;

    public ExactBookCopyService(ExactBookCopyRepository exactBookCopyRepository,
                                BookEditionRepository bookEditionRepository) {
        this.exactBookCopyRepository = exactBookCopyRepository;
        this.bookEditionRepository = bookEditionRepository;
    }

    @Transactional
    public ExactBookCopyResponse createExactBookCopy(CreateExactBookCopyRequest request) {
        BookEdition bookEdition = bookEditionRepository.findById(request.bookEditionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book edition not found with ID: " + request.bookEditionId()));

        CopyStatus status = request.status() != null ? request.status() : CopyStatus.AVAILABLE;

        ExactBookCopy copy = new ExactBookCopy(bookEdition, status);
        ExactBookCopy saved = exactBookCopyRepository.save(copy);
        return toResponse(saved);
    }

    public ExactBookCopyResponse getExactBookCopyById(Long id) {
        ExactBookCopy copy = exactBookCopyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found"));
        return toResponse(copy);
    }

    public List<ExactBookCopyResponse> getAllExactBookCopies() {
        return exactBookCopyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ExactBookCopyResponse> getExactBookCopiesByEditionId(Long bookEditionId) {
        return exactBookCopyRepository.findByBookEditionId(bookEditionId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ExactBookCopyResponse> getAvailableCopies() {
        return exactBookCopyRepository.findByStatus(CopyStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExactBookCopyResponse updateCopyStatus(Long id, CopyStatus newStatus) {
        ExactBookCopy copy = exactBookCopyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found"));
        copy.setStatus(newStatus);
        ExactBookCopy saved = exactBookCopyRepository.save(copy);
        return toResponse(saved);
    }

    @Transactional
    public void deleteExactBookCopy(Long id) {
        ExactBookCopy copy = exactBookCopyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found"));
        exactBookCopyRepository.delete(copy);
    }

    private ExactBookCopyResponse toResponse(ExactBookCopy copy) {
        BookEdition edition = copy.getBookEdition();
        return new ExactBookCopyResponse(
                copy.getId(),
                edition.getId(),
                edition.getIsbn().getValue(),
                edition.getBook().getTitle(),
                copy.getStatus()
        );
    }
}
