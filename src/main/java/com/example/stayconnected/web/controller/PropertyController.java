package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.PropertyEditRequest;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public PropertyController(PropertyService propertyService, ReviewService reviewService, UserService userService) {
        this.propertyService = propertyService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getPropertiesPage() {

        // Call propertyService.getAllProperties()

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/properties");


        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPropertyDetails(@PathVariable UUID id) {

        // Get property by id
        // Get all reviews for that property (call reviewService)

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/property-details");

        // Add objects: property, List<Review> reviews


        return modelAndView;
    }

    @PostMapping("/{id}/review")
    public ModelAndView addReview(@PathVariable UUID id,
                                  @Valid @ModelAttribute CreateReviewRequest request,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("property/property-details");
            modelAndView.addObject("property", this.propertyService.getById(id));
            modelAndView.addObject("reviews", this.reviewService.getAllReviewsByPropertyWithId(id));
            return modelAndView;
        }

        // Get user by his id (logged-in user)

        // Call the addReview method : this.reviewService.addReview(userId, propertyId, request);

        return new ModelAndView("redirect:/properties/" + id);
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView showPropertyCreateForm(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User authUser = this.userService.getUserById(userPrincipal.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/create-property-form");
        modelAndView.addObject("authUser", authUser);
        modelAndView.addObject("createPropertyRequest", new CreatePropertyRequest());
        return modelAndView;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView createProperty(@Valid CreatePropertyRequest createPropertyRequest,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        if (createPropertyRequest.getImages() == null
                || createPropertyRequest.getImages().isEmpty()
                || createPropertyRequest.getImages().stream().allMatch(MultipartFile::isEmpty)) {

            bindingResult.rejectValue("images", "images.empty", "Please upload at least one image");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("property/create-property-form");
            modelAndView.addObject("authUser", user);
            return modelAndView;
        }

        Property property = this.propertyService.createProperty(createPropertyRequest, user);

        // Loop through request.getImages() and save them to the db


        return new ModelAndView("redirect:/properties/" + property.getId());
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminProperties() {

        /*
            Going to use @AuthenticationPrincipal and AuthenticationMetadata
            to get the logged in user id which we will put in the parameters of
            propertyService - to get all properties the admin owns
         */

        // Call propertyService to get all properties the admin owns

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/my-properties");

        // Add properties as object

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('Admin')")
    public ModelAndView getPropertyEditForm(@PathVariable UUID id) {

        // Get Property by Id
        // Create a DTO and set value to each field from the property

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/property-edit-form");

        // Add dto as object and use its fields in Thymeleaf

        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPropertyChanges(@PathVariable UUID id,
                                              @Valid @ModelAttribute
                                              PropertyEditRequest propertyEditRequest,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("property/property-edit-form");
        }


        // Call propertyService method that accepts parameters the ID and the PropertyEditRequest


       return new ModelAndView("redirect:/properties/" + id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProperty(@PathVariable UUID id) {

        // Delete the property by id

        return "redirect:/properties/my-properties";
    }





}
