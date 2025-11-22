package com.example.stayconnected.review.service.impl;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.repository.ReviewRepository;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utility.exception.PropertyDoesNotExist;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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


    @Override
    public Review getReviewById(UUID id) {
        return this.reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public List<Review> getAllReviewsByPropertyWithId(UUID id) {
        return this.reviewRepository.findAllByPropertyIdOrderByCreatedAtDesc(id);
    }

    @Override
    public void addReview(UUID userId, UUID propertyId, CreateReviewRequest request) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyDoesNotExist("Property with such id [%s] does not exist"
                        .formatted(propertyId)));

        Review review = new Review  (
                                            request.getComment(),
                                            request.getRating(),
                                            user,
                                            property
                                    );

        this.reviewRepository.save(review);

        BigDecimal averageRating = getAverageRatingForProperty(propertyId);

        property.setAverageRating(averageRating);

        this.propertyRepository.save(property);

        log.info("Successfully added review with id [%s] from user with id [%s] to property with id [%s]"
                .formatted(review.getId(), user.getId(), property.getId()));
    }

    @Override
    public BigDecimal getAverageRatingForProperty(UUID propertyId) {
        return this.reviewRepository.findAverageRatingForProperty(propertyId);
    }

    @Override
    public void deleteReview(Review review) {

        Property property = review.getProperty();

        this.reviewRepository.delete(review);

        BigDecimal newAverageRating = getAverageRatingForProperty(property.getId());

        property.setAverageRating(newAverageRating);
        this.propertyRepository.save(property);

        log.info("Successfully deleted review of property with id [%s]"
                .formatted(review.getProperty().getId()));
    }


    @Override
    public List<Object[]> getAverageRatingsForProperties(List<UUID> propertyIds) {
        return this.reviewRepository.findAverageRatingsForProperties(propertyIds);
    }

    @Override
    public List<Review> getLast5ReviewsForProperty(UUID propertyId) {
        return this.reviewRepository.findTop5ByPropertyIdOrderByCreatedAtDesc(propertyId);
    }

}
