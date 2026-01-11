package agh.oot.librarby.rental.mapper;

import agh.oot.librarby.rental.dto.RentalResponse;
import agh.oot.librarby.rental.model.Rental;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Component
public class RentalResponseMapper {

    public RentalResponse toDto(Rental rental) {
        Objects.requireNonNull(rental, "rental must not be null");

        var copy = rental.getExactBookCopy();
        var edition = copy.getBookEdition();
        var book = edition.getBook();

        // Choose one convention and keep it consistent across the project.
        // If we want UTC everywhere, use ZoneOffset.UTC instead.
        ZoneId zone = ZoneId.systemDefault();

        return new RentalResponse(
                rental.getId(),
                rental.getReader().getId(),
                copy.getId(),
                edition.getId(),
                book.getId(),
                book.getTitle(),
                rental.getStatus(),
                rental.getDueDate(),
                LocalDateTime.ofInstant(rental.getRentedAt(), zone),
                rental.getReturnedAt() == null
                        ? null
                        : LocalDateTime.ofInstant(rental.getReturnedAt(), zone)
        );
    }
}
