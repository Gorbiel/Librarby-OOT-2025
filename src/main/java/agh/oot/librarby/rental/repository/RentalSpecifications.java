package agh.oot.librarby.rental.repository;

import agh.oot.librarby.rental.model.Rental;
import agh.oot.librarby.rental.model.RentalStatus;
import org.springframework.data.jpa.domain.Specification;

public final class RentalSpecifications {

    private RentalSpecifications() {}

    public static Specification<Rental> hasReaderId(Long readerId) {
        return (root, query, cb) ->
                readerId == null ? null : cb.equal(root.get("reader").get("id"), readerId);
    }

    public static Specification<Rental> hasStatus(RentalStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Rental> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            return active ? cb.isNull(root.get("returnedAt")) : cb.isNotNull(root.get("returnedAt"));
        };
    }

    public static Specification<Rental> hasBookId(Long bookId) {
        return (root, query, cb) -> {
            if (bookId == null) return null;

            // rental -> exactBookCopy -> bookEdition -> book -> id
            var copyJoin = root.join("exactBookCopy");
            var editionJoin = copyJoin.join("bookEdition");
            var bookJoin = editionJoin.join("book");

            return cb.equal(bookJoin.get("id"), bookId);
        };
    }
}
