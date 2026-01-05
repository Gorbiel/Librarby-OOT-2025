package agh.oot.librarby.book.repository;

import agh.oot.librarby.book.model.CopyStatus;
import agh.oot.librarby.book.model.ExactBookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExactBookCopyRepository extends JpaRepository<ExactBookCopy, Long> {
    List<ExactBookCopy> findByBookEditionId(Long bookEditionId);

    List<ExactBookCopy> findByStatus(CopyStatus status);

    List<ExactBookCopy> findByBookEditionIdAndStatus(Long bookEditionId, CopyStatus status);
}
