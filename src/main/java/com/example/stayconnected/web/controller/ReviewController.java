package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, PropertyService propertyService1) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.propertyService = propertyService1;
    }

    @DeleteMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable UUID reviewId) {

        Review review = this.reviewService.getReviewById(reviewId);
        UUID propertyId = review.getProperty().getId();

        this.reviewService.deleteReview(review);

        return "redirect:/properties/" + propertyId;
    }

    @PostMapping("/create/{propertyId}")
    public ModelAndView addReview(@PathVariable UUID propertyId,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @Valid CreateReviewRequest createReviewRequest,
                                  BindingResult bindingResult) {



        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {

            User user = this.userService.getUserById(userPrincipal.getId());
            Property property = this.propertyService.getById(propertyId);
            User propertyOwner = property.getOwner();

            List<PropertyImage> gridImages = new ArrayList<>();

            List<Review> last5Reviews = this.reviewService.getLast5ReviewsForProperty(property.getId());
            int allReviewsCount = this.reviewService.getAllReviewsByPropertyWithId(property.getId()).size();

            if (property.getImages().size() > 1) {
                gridImages = property.getImages().subList(1,2);
            }

            modelAndView.setViewName("/property/property-details");
            modelAndView.addObject("authUser", user);
            modelAndView.addObject("property", property);
            modelAndView.addObject("propertyOwner", propertyOwner);
            modelAndView.addObject("gridImages", gridImages);
            modelAndView.addObject("last5Reviews", last5Reviews);
            modelAndView.addObject("countReviews", allReviewsCount);

            return modelAndView;
        }

        this.reviewService.addReview(userPrincipal.getId(), propertyId, createReviewRequest);





        return new ModelAndView("redirect:/properties/" + propertyId);
    }




}
