package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.web.dto.CreatePropertyRequest;
import com.example.stayconnected.web.dto.PropertyEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ReviewService reviewService;

    @Autowired
    public PropertyController(PropertyService propertyService, ReviewService reviewService) {
        this.propertyService = propertyService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ModelAndView getPropertiesPage() {

        // Call propertyService.getAllProperties()

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("properties");


        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPropertyDetails(@PathVariable UUID id) {

        // Get property by id
        // Get all reviews for that property (call reviewService)

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property-details");

        // Add objects: property, List<Review> reviews


        return modelAndView;
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView createPropertyForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/create-property-form");
        modelAndView.addObject("createPropertyRequest", new CreatePropertyRequest());
        return modelAndView;
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView createPropertyPost(@Valid @ModelAttribute CreatePropertyRequest createPropertyRequest,
                                           BindingResult bindingResult) {


        // Do validation on the request from the form (by calling bindingResult.hasErrors())

        // If it has errors , then set the view of modelAndView = "create-property-form" and return the modelAndView

        // Call a method from propertyService that will save the property(PropertyRequest -> Property)


        return new ModelAndView("redirect:/properties/my-properties");
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
        // Create a DTO and set value to each field

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/property-edit-form");

        // Add dto as object and use its fields in Thymeleaf

        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPropertyChanges(@PathVariable UUID id,
                                              @Valid @ModelAttribute("propertyEditRequest")
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
