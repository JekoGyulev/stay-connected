package com.example.stayconnected.review.service;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ReviewService {

    Review getReviewById(UUID id);

    List<Review> getAllReviewsByPropertyWithId(UUID id);

    void addReview(UUID userId, UUID propertyId, CreateReviewRequest request);

    void deleteReview(Review review);

    List<Object[]> getAverageRatingsForProperties(List<UUID> propertyIds);

    List<Review> getLast5ReviewsForProperty(UUID propertyId);

    BigDecimal getAverageRatingForProperty(UUID propertyId);

    void deleteAllReviewsForProperty(UUID id);
}
