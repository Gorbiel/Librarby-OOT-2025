package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthYear(String firstName, String lastName, Year birthYear);
}
