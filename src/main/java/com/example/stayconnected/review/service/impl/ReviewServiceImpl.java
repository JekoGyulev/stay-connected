package com.example.stayconnected.review.service.impl;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.repository.ReviewRepository;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    // Have at least 1 logging message -> log.info("Successfully added review")
    // Have at least 1 logging message -> log.info("Successfully deleted review")


    @Override
    public void addReview(UUID userId, UUID propertyId, CreateReviewRequest request) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Review review = new Review  (
                                            request.getComment(),
                                            request.getRating(),
                                            user,
                                            property
                                    );

        this.reviewRepository.save(review);

        log.info("Successfully added review with id [%s] from user with id [%s] to property with id [%s]"
                .formatted(review.getId(), user.getId(), property.getId()));
    }

}
