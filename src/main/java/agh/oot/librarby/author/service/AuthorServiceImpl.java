package agh.oot.librarby.author.service;

import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.author.mapper.MultipleAuthorsResponseMapper;
import agh.oot.librarby.author.model.Author;
import agh.oot.librarby.author.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final MultipleAuthorsResponseMapper multipleAuthorsResponseMapper;

    public AuthorServiceImpl(
            AuthorRepository authorRepository,
            MultipleAuthorsResponseMapper multipleAuthorsResponseMapper
    ) {
        this.authorRepository = authorRepository;
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
}
