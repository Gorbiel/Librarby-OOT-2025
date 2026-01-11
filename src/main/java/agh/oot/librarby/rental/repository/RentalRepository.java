package agh.oot.librarby.rental.repository;

import agh.oot.librarby.rental.model.Rental;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {

    @EntityGraph(value = "Rental.withDetails")
    List<Rental> findAll(Specification<Rental> spec);

    @Override
    @EntityGraph(value = "Rental.withDetails")
    List<Rental> findAll();

    @Override
    @EntityGraph(value = "Rental.withDetails")
    Optional<Rental> findById(Long id);
}
