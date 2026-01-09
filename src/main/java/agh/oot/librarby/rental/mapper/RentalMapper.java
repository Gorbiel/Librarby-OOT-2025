package agh.oot.librarby.rental.mapper;

import agh.oot.librarby.rental.dto.RentalResponse;
import agh.oot.librarby.rental.model.Rental;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class RentalMapper {

    private RentalMapper() {}

    public static RentalResponse toResponse(Rental rental) {
        var copy = rental.getExactBookCopy();
        var edition = copy.getBookEdition();
        var book = edition.getBook();

        return new RentalResponse(
                rental.getId(),
                rental.getReader().getId(),
                copy.getId(),
                edition.getId(),
                book.getId(),
                book.getTitle(),
                rental.getStatus(),
                rental.getDueDate(),
                LocalDateTime.ofInstant(rental.getRentedAt(), ZoneOffset.UTC),
                rental.getReturnedAt() == null ? null : LocalDateTime.ofInstant(rental.getReturnedAt(), ZoneOffset.UTC)
        );
    }
}
