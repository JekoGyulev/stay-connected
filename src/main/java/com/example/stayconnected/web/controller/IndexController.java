package com.example.stayconnected.web.controller;

import com.example.stayconnected.web.dto.location.CityStatsDTO;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final LocationService locationService;

    @Autowired
    public IndexController(UserService userService, PropertyService propertyService, LocationService locationService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public ModelAndView modelAndView(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<Property> featuredProperties = this.propertyService.getFeaturedProperties();

        List<CityStatsDTO> mostPopularDestinations = this.locationService.get4MostPopularDestinations();

        // Make boolean flags : isSuperhost, isNew, isFeatured (see the table) and then put them in as models


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("featuredProperties", featuredProperties);
        modelAndView.addObject("mostPopularDestinations", mostPopularDestinations);

        return modelAndView;
    }

    @GetMapping("/terms-and-condition")
    public String showTermsAndConditionsPage() {
        return "terms-and-condition";
    }


}
