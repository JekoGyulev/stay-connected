package com.example.stayconnected;


import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.repository.LocationRepository;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.repository.ReviewRepository;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.utils.exception.PropertyDoesNotExist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeleteReviewITest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;


    @Test
    void whenDeleteReview_shouldInvokeServiceMethod_andChangeTheAverageRatingOfProperty() {


        User user = User.builder()
                .firstName("John")
                .lastName("John")
                .email("John123@gmail.com")
                .password("John123456")
                .username("John17")
                .isActive(true)
                .role(UserRole.ADMIN)
                .registeredAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Location location = Location.builder().country("Country").city("City").address("Address").build();

        locationRepository.save(location);

        Property property = Property.builder()
                .title("Title")
                .description("Description")
                .owner(user)
                .pricePerNight(BigDecimal.valueOf(50))
                .categoryType(CategoryType.APARTMENT)
                .amenities(new ArrayList<>(List.of("Amenity1", "Amenity2")))
                .location(location)
                .createDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        propertyRepository.save(property);

        Review review = Review.builder()
                .rating(5)
                .comment("Commented from someone test!")
                .createdFrom(user)
                .property(property)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        property.setAverageRating(BigDecimal.valueOf(review.getRating()));

        propertyRepository.save(property);

        BigDecimal averageRatingBeforeReviewDelete = property.getAverageRating();

        long sizeBeforeReviewDelete = reviewRepository.findAllByPropertyIdOrderByCreatedAtDesc(property.getId()).size();

        reviewService.deleteReview(review);


        long sizeAfterReviewDelete = reviewRepository.findAllByPropertyIdOrderByCreatedAtDesc(property.getId()).size();

        Property updatedProperty = propertyRepository.findById(property.getId())
                .orElseThrow(() -> new PropertyDoesNotExist("Property does not exist"));

        BigDecimal averageRatingAfterReviewDelete = updatedProperty.getAverageRating();


        assertEquals(sizeBeforeReviewDelete - 1, sizeAfterReviewDelete);
        assertNotEquals(averageRatingBeforeReviewDelete, averageRatingAfterReviewDelete);
    }






}
