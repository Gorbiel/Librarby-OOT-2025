package agh.oot.librarby.book.mapper;

import agh.oot.librarby.book.dto.*;
import agh.oot.librarby.book.model.BookEdition;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class BookEditionResponseMapper {
    public BookEditionResponse toDto(BookEdition bookEdition) {
        return new BookEditionResponse(
                bookEdition.getId(),
                bookEdition.getIsbn(),
                new BookBriefResponse(bookEdition.getBook().getId(), bookEdition.getBook().getTitle()),
                bookEdition.getPageCount(),
                bookEdition.getPublicationYear(),
                new PublisherResponse(bookEdition.getPublisher().getId(), bookEdition.getPublisher().getName()),
                bookEdition.getLanguage());
    }

    public MultipleBookEditionResponse listToDto(List<BookEdition> bookEditionList) {
        Objects.requireNonNull(bookEditionList, "book editions list must not be null");

        List<BookEditionResponse> items = bookEditionList.stream()
                .map(this::toDto)
                .toList();

        return new MultipleBookEditionResponse(items);
    }
}
