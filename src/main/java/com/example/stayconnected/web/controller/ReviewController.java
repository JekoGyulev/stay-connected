package com.example.stayconnected.web.controller;

import com.example.stayconnected.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String addReview(@PathVariable UUID propertyId) {

        // Call reviewService's method


        return "redirect:/properties/" + propertyId;
    }

    @DeleteMapping("/{reviewId}/{propertyId}")
    public String deleteReview(@PathVariable UUID reviewId, @PathVariable UUID propertyId) {

        // Call reviewService's method

        return "redirect:/properties/" + propertyId;
    }




}
