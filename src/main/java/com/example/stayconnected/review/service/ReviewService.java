package com.example.stayconnected.review.service;

import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;

import java.util.UUID;

public interface ReviewService {

    Review getReviewById(UUID id);

    // Get all reviews for given property (use the reviewRepository.findAllByPropertyId method)

    // Add review
    void addReview(UUID userId, UUID propertyId, CreateReviewRequest request);

    // Delete review
    void deleteReview(Review review);
}
