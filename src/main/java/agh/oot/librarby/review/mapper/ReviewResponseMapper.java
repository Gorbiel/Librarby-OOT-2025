package agh.oot.librarby.review.mapper;

import agh.oot.librarby.review.dto.ReviewResponse;
import agh.oot.librarby.review.model.Review;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReviewResponseMapper {

    public ReviewResponse toDto(Review review) {
        Objects.requireNonNull(review, "review must not be null");

        return new ReviewResponse(
                review.getId(),
                review.getBook().getId(),
                review.getReader().getId(),
                review.getBookEdition() != null ? review.getBookEdition().getId() : null,
                review.getRating(),
                review.getText(),
                review.getCreatedAt(),
                review.getVerified()
        );
    }
}

