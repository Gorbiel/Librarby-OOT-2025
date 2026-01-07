package agh.oot.librarby.book.service;


import agh.oot.librarby.book.dto.BookBriefResponse;
import agh.oot.librarby.book.dto.BookEditionResponse;
import agh.oot.librarby.book.dto.PublisherResponse;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.book.repository.BookEditionRepository;
import agh.oot.librarby.user.model.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookEditionService {
    private final BookEditionRepository bookEditionRepository;

    public BookEditionService(BookEditionRepository bookEditionRepository) {
        this.bookEditionRepository = bookEditionRepository;
    }

    public BookEditionResponse getBookEdition(long bookEditionId) {
        BookEdition bookEdition = bookEditionRepository.findById(bookEditionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookEdition not found"));

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
}
