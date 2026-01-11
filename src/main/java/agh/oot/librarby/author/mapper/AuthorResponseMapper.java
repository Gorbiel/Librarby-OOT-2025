package agh.oot.librarby.author.mapper;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.model.Author;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthorResponseMapper {

    public AuthorResponse toDto(Author author) {
        Objects.requireNonNull(author, "author must not be null");
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getMiddleName(),
                author.getLastName()
        );
    }
}
