package agh.oot.librarby.author.mapper;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.author.model.Author;

import java.util.List;

public class MultipleAuthorsResponseMapper {
    private final AuthorResponseMapper authorMapper;

    public MultipleAuthorsResponseMapper(AuthorResponseMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public MultipleAuthorsResponse toDto(List<Author> authors) {
        List<AuthorResponse> items = authors.stream()
                .map(authorMapper::toDto)
                .toList();
        return new MultipleAuthorsResponse(items);
    }
}
