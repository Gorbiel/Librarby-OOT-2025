package agh.oot.librarby.book.mapper;

import agh.oot.librarby.book.dto.AuthorResponse;
import agh.oot.librarby.book.model.Author;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthorResponseMapper {

    public AuthorResponse toDto(Author author) {
        Objects.requireNonNull(author, "author must not be null");
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}
