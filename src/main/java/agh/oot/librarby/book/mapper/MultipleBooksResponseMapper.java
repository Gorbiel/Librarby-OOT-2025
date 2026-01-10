package agh.oot.librarby.book.mapper;

import agh.oot.librarby.book.dto.BookResponse;
import agh.oot.librarby.book.dto.MultipleBooksResponse;
import agh.oot.librarby.book.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultipleBooksResponseMapper {

    private final BookResponseMapper bookMapper;

    public MultipleBooksResponseMapper(BookResponseMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public MultipleBooksResponse toDto(List<Book> books) {
        List<BookResponse> items = books.stream()
                .map(bookMapper::toDto)
                .toList();
        return new MultipleBooksResponse(items);
    }
}
