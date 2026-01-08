package agh.oot.librarby.reservation.specification;

import agh.oot.librarby.reservation.model.Reservation;
import agh.oot.librarby.reservation.model.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

public class ReservationSpecifications {

    private ReservationSpecifications() {
        // Utility class - private constructor
    }

    public static Specification<Reservation> hasReaderId(Long readerId) {
        return (root, query, criteriaBuilder) ->
                readerId == null ? null : criteriaBuilder.equal(root.get("reader").get("id"), readerId);
    }

    public static Specification<Reservation> hasBookId(Long bookId) {
        return (root, query, criteriaBuilder) ->
                bookId == null ? null : criteriaBuilder.equal(root.get("book").get("id"), bookId);
    }

    public static Specification<Reservation> hasStatus(ReservationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
}

