package agh.oot.librarby.publisher.repository;

import agh.oot.librarby.publisher.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByNameIgnoreCase(String name);

    List<Publisher> findByNameContainingIgnoreCase(String q);
}
