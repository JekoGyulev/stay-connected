package com.example.stayconnected.web.controller;

import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.property.FilterPropertyRequest;
import com.example.stayconnected.web.dto.property.HomeSearchPropertyRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final LocationService locationService;
    private final ReservationService reservationService;

    @Autowired
    public PropertyController(PropertyService propertyService, ReviewService reviewService, UserService userService, LocationService locationService, ReservationService reservationService) {
        this.propertyService = propertyService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.locationService = locationService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ModelAndView getPropertiesPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = this.userService.getUserById(userPrincipal.getId());

        List<Property> properties = this.propertyService.getAllProperties();

        List<String> allCountries = this.locationService.getAllDistinctCountries();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/properties");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("properties", properties);
        modelAndView.addObject("countries", allCountries);
        modelAndView.addObject("filterPropertyRequest", new FilterPropertyRequest());

        return modelAndView;
    }

    @GetMapping("/filter")
    public ModelAndView getFilteredProperties(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              FilterPropertyRequest filterPropertyRequest) {
        User user = this.userService.getUserById(userPrincipal.getId());

        List<String> allCountries = this.locationService.getAllDistinctCountries();

        List<Property> filteredProperties = this.propertyService.getFilteredProperties(filterPropertyRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/properties");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("properties", filteredProperties);
        modelAndView.addObject("countries", allCountries);
        modelAndView.addObject("filterPropertyRequest", filterPropertyRequest);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView getPropertiesMatchSearch(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 HomeSearchPropertyRequest homeSearchPropertyRequest) {

        User user = this.userService.getUserById(userPrincipal.getId());
        FilterPropertyRequest filterPropertyRequest = new FilterPropertyRequest("", homeSearchPropertyRequest.getCountry());

        List<UUID> unavailableToBookPropertyIds = this.reservationService.getUnavailableToBookPropertyIds(homeSearchPropertyRequest.getCheckIn(), homeSearchPropertyRequest.getCheckOut());

        List<Property> availableToBookProperties = this.propertyService.getAvailableToBookProperties(unavailableToBookPropertyIds, homeSearchPropertyRequest.getCountry());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/properties");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("countries", this.locationService.getAllDistinctCountries());
        modelAndView.addObject("filterPropertyRequest", filterPropertyRequest);
        modelAndView.addObject("properties", availableToBookProperties);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPropertyDetails(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @RequestParam(value = "message", required = false) String message) {

        User user = this.userService.getUserById(userPrincipal.getId());
        Property property = this.propertyService.getById(id);
        User propertyOwner = property.getOwner();

        List<PropertyImage> gridImages = new ArrayList<>();

        if (property.getImages().size() > 1) {
            gridImages = property.getImages().subList(1,property.getImages().size());
        }

        List<Review> last5Reviews = this.reviewService.getLast5ReviewsForProperty(property.getId());
        int allReviewsCount = this.reviewService.getAllReviewsByPropertyWithId(property.getId()).size();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/property-details");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("propertyOwner", propertyOwner);
        modelAndView.addObject("property", property);
        modelAndView.addObject("gridImages", gridImages);
        modelAndView.addObject("last5Reviews", last5Reviews);
        modelAndView.addObject("countReviews", allReviewsCount);
        modelAndView.addObject("createReviewRequest", new CreateReviewRequest());
        modelAndView.addObject("createReservationRequest",  new CreateReservationRequest());

        if (message != null) {
            modelAndView.addObject("message", message);
        }

        return modelAndView;
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

        return new ModelAndView("redirect:/properties/" + property.getId());
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminProperties(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @RequestParam(value = "message", required = false) String message) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<Property> ownedProperties = this.propertyService.getPropertiesByAdminId(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("property/manage-properties");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("properties", ownedProperties);

        if (message != null) {
            modelAndView.addObject("message", message);
        }

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getPropertyEditForm(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Property property = this.propertyService.getById(id);

        User user = this.userService.getUserById(userPrincipal.getId());

        EditPropertyRequest editPropertyRequest = DtoMapper.fromProperty(property);

        ModelAndView modelAndView = new ModelAndView("property/property-edit-form");
        modelAndView.addObject("authUser", user);
        modelAndView.addObject("propertyId", property.getId());
        modelAndView.addObject("editPropertyRequest", editPropertyRequest);

        return modelAndView;
    }

    @PatchMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editProperty(@PathVariable UUID id,
                                     @AuthenticationPrincipal UserPrincipal userPrincipal,
                                     @Valid EditPropertyRequest editPropertyRequest,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            User user = this.userService.getUserById(userPrincipal.getId());

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("property/property-edit-form");
            modelAndView.addObject("authUser", user);
            modelAndView.addObject("propertyId", id);
            modelAndView.addObject("editPropertyRequest", editPropertyRequest);

            return modelAndView;
        }


        this.propertyService.editProperty(id, editPropertyRequest);


        return new ModelAndView("redirect:/properties/" + id + "?message=Successfully edited property");
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProperty(@PathVariable UUID id) {
        Property property = this.propertyService.getById(id);
        this.propertyService.deleteProperty(property);
        return "redirect:/properties/my-properties?message=Successfully deleted property!";
    }





}
