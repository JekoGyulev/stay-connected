package com.example.stayconnected.review.service;

import com.example.stayconnected.web.dto.review.CreateReviewRequest;

import java.util.UUID;

public interface ReviewService {

    // Get all reviews for given property (use the reviewRepository.findAllByPropertyId method)

    // Add review
    void addReview(UUID userId, UUID propertyId, CreateReviewRequest request);

    // Delete review
}
