package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService, PropertyService propertyService) {
        this.reviewService = reviewService;
    }

    @DeleteMapping("/{reviewId}")
    public String deleteReview(@PathVariable UUID reviewId) {
        Review review = this.reviewService.getReviewById(reviewId);

        this.reviewService.deleteReview(review);

        UUID propertyId = review.getProperty().getId();
        return "redirect:/properties/" + propertyId;
    }




}
