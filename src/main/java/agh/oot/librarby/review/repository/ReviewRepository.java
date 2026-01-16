package agh.oot.librarby.review.repository;

import agh.oot.librarby.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
