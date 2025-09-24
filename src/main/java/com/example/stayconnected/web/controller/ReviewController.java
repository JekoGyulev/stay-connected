package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/{propertyId}")
    public String addReview(@PathVariable UUID propertyId,
                            @Valid @ModelAttribute CreateReviewRequest request,
                            BindingResult bindingResult) {

        // Check for errors

        // Get user by his id (logged-in user)

        // Call the addReview method


        return "redirect:/properties/" + propertyId;
    }

    @DeleteMapping("/{reviewId}")
    public String deleteReview(@PathVariable UUID reviewId) {
        Review review = this.reviewService.getReviewById(reviewId);

        this.reviewService.deleteReview(review);

        UUID propertyId = review.getProperty().getId();
        return "redirect:/properties/" + propertyId;
    }




}
