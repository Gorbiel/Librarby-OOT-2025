package agh.oot.librarby.book.mapper;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.mapper.AuthorResponseMapper;
import agh.oot.librarby.book.dto.BookResponse;
import agh.oot.librarby.book.model.Book;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookResponseMapper {

    private final AuthorResponseMapper authorMapper;

    public BookResponseMapper(AuthorResponseMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public BookResponse toDto(Book book) {
        Objects.requireNonNull(book, "book must not be null");

        Set<AuthorResponse> authors = book.getAuthors().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toSet());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getGenres(),     // defensive copy already in entity getter
                book.getAgeRating(),
                authors
        );
    }
}
