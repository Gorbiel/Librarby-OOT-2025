package agh.oot.librarby.rental.repository;

import agh.oot.librarby.rental.model.Rental;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {

    @Override
    @EntityGraph(attributePaths = {
            "reader",
            "exactBookCopy",
            "exactBookCopy.bookEdition",
            "exactBookCopy.bookEdition.book"
    })
    List<Rental> findAll(Specification<Rental> spec);
}
