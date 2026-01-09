package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.ExactBookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExactBookCopyRepository extends JpaRepository<ExactBookCopy, Long> {
}

