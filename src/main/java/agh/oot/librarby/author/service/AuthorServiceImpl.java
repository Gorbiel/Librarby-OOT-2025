package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.author.mapper.AuthorResponseMapper;
import agh.oot.librarby.author.mapper.MultipleAuthorsResponseMapper;
import agh.oot.librarby.author.model.Author;
import agh.oot.librarby.author.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorResponseMapper authorResponseMapper;
    private final MultipleAuthorsResponseMapper multipleAuthorsResponseMapper;

    public AuthorServiceImpl(
            AuthorRepository authorRepository,
            AuthorResponseMapper authorResponseMapper,
            MultipleAuthorsResponseMapper multipleAuthorsResponseMapper
    ) {
        this.authorRepository = authorRepository;
        this.authorResponseMapper = authorResponseMapper;
        this.multipleAuthorsResponseMapper = multipleAuthorsResponseMapper;
    }

    @Override
    public MultipleAuthorsResponse listAuthors(String q) {
        List<Author> authors;

        if (q == null || q.isBlank()) {
            authors = authorRepository.findAll();
        } else {
            String query = q.trim();
            authors = authorRepository.searchByName(query);
        }

        return multipleAuthorsResponseMapper.toDto(authors);
    }

    @Override
    public AuthorResponse getAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found")
                );

        return authorResponseMapper.toDto(author);
    }
}
