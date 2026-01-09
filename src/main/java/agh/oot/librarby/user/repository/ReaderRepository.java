package agh.oot.librarby.user.repository;

import agh.oot.librarby.user.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
}
