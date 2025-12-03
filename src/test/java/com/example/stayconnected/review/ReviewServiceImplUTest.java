package com.example.stayconnected.review;


import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.repository.ReviewRepository;
import com.example.stayconnected.review.service.impl.ReviewServiceImpl;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.utils.exception.PropertyDoesNotExist;
import com.example.stayconnected.utils.exception.UserDoesNotExist;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplUTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;


    @Test
    void whenReviewDoesNotExist_thenThrowException() {

        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder()
                .id(reviewId)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewServiceImpl.getReviewById(reviewId));
    }


    @Test
    void whenReviewExist_thenReturnReview() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder()
                .id(reviewId)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertEquals(reviewId, reviewServiceImpl.getReviewById(reviewId).getId());
        assertNotNull(reviewServiceImpl.getReviewById(reviewId));
    }


    @Test
    void whenThereAreReviewsForProperty_thenReturnThemAll() {

        UUID propertyId = UUID.randomUUID();
        Property property = Property.builder().id(propertyId).build();

        Review review = Review.builder().property(property).build();
        Review review2 = Review.builder().property(property).build();
        List<Review> reviews =  Arrays.asList(review, review2);

        when(reviewRepository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId)).thenReturn(reviews);

        List<Review> result = reviewServiceImpl.getAllReviewsByPropertyWithId(propertyId);

        assertEquals(reviews.size(), result.size());
        assertArrayEquals(reviews.toArray(), result.toArray());
    }

    @Test
    void whenThereAreNoReviewsForProperty_thenReturnEmptyArray() {
        UUID propertyId = UUID.randomUUID();

        when(reviewRepository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId)).thenReturn(Collections.emptyList());

        assertEquals(0, reviewServiceImpl.getAllReviewsByPropertyWithId(propertyId).size());
    }



    @Test
    void whenAddReview_andUserIsNotFound_thenThrowException() {
        UUID userId =  UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        CreateReviewRequest dto = CreateReviewRequest.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExist.class, () -> reviewServiceImpl.addReview(userId, propertyId, dto));
    }


    @Test
    void whenAddReview_andPropertyIsNotFound_thenThrowException() {
        UUID userId =  UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        CreateReviewRequest dto = CreateReviewRequest.builder().build();

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(PropertyDoesNotExist.class, () -> reviewServiceImpl.addReview(userId, propertyId, dto));
    }


    @Test
    void whenAddReview_andUserIsFound_andPropertyIsFound_thenAddReview() {

        UUID userId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Property property = Property.builder().id(propertyId).build();

        CreateReviewRequest dto = CreateReviewRequest.builder()
                .comment("Hello world!")
                .rating(4)
                .build();


        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(reviewRepository.save(any())).thenReturn(review);

        reviewServiceImpl.addReview(userId, propertyId, dto);

        verify(reviewRepository).save(argThat(r ->
                r.getComment().equals(dto.getComment()) &&
                        r.getRating() == dto.getRating() &&
                        r.getCreatedFrom().equals(user) &&
                        r.getProperty().equals(property)
        ));

        verify(reviewRepository).save(any(Review.class));
    }


    @Test
    void whenThereAreReviewsForProperty_andDeleteThemAll_thenDeleteAllReviews () {

        Property p = Property.builder().id(UUID.randomUUID()).build();

        Review review1 =  Review.builder().property(p).build();
        Review review2 =  Review.builder().property(p).build();

        reviewServiceImpl.deleteAllReviewsForProperty(p.getId());

        verify(reviewRepository).deleteReviewByProperty_Id(p.getId());
    }

    @Test
    void whenThereIsReview_andDelete_thenDeleteReview() {

        Property property = Property.builder().id(UUID.randomUUID()).build();

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .property(property)
                .build();

        reviewServiceImpl.deleteReview(review);

        verify(reviewRepository).delete(review);
        verify(propertyRepository).save(property);
    }

    @Test
    void getLast5ReviewsForProperty_returnsTop5Reviews() {
        Property property = Property.builder().id(UUID.randomUUID()).build();

        Review review1  =  Review.builder().property(property).build();
        Review review2  =  Review.builder().property(property).build();
        Review review3  =  Review.builder().property(property).build();
        Review review4  =  Review.builder().property(property).build();
        Review review5  =  Review.builder().property(property).build();

        List<Review> reviews =   Arrays.asList(review1, review2, review3, review4, review5);

        when(reviewRepository.findTop5ByPropertyIdOrderByCreatedAtDesc(property.getId())).thenReturn(reviews);

        List<Review> result = reviewServiceImpl.getLast5ReviewsForProperty(property.getId());

        assertEquals(5,  result.size());
    }

}
