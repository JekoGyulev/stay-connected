package com.example.stayconnected.web.controller;

import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final ReservationService reservationService;
    private final UserService userService;

    @Autowired
    public UserController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getUserProfilePage(@PathVariable UUID id) {

        // Get user by ID
        // Shows user profile details
        // Will be used to show the ID, registered,lastLoggedIn of user

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-details");

        return modelAndView;
    }

    @GetMapping("/{id}/profile/edit")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {
        User user = this.userService.getUserById(id);

        ProfileEditRequest profileEditRequest = DtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-edit-form");
        modelAndView.addObject("profileEditRequest", profileEditRequest);
        modelAndView.addObject("user", user); // For displaying username, id above

        return modelAndView;
    }
    @PutMapping("/{id}/profile/edit")
    public ModelAndView editProfile(@PathVariable UUID id,
                                    @Valid @ModelAttribute ProfileEditRequest profileEditRequest,
                                    BindingResult bindingResult
                                    ) {

        User user = this.userService.getUserById(id);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("user/profile-edit-form");
            modelAndView.addObject("user", user); // Because of lastLoggedIn, and isActive
            return modelAndView;
        }

        this.userService.updateProfile(user, profileEditRequest);

        return new ModelAndView("redirect:/users/" + id + "/profile");
    }

    @GetMapping("/table")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getUsersTablePage() {

        // Fetch all users which we put later as objects in thymeleaf and use th:each in HTML table

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/users");

        // Add the users as object to modelAndView

        return modelAndView;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getStatsPage() {

        // Fetch stats for : total users, total hosted properties,
        // total bookings made (look at smart wallet's way of stats page)

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/stats");

        return modelAndView;
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserRole(@PathVariable UUID id) {
        this.userService.switchRole(id);
        return "redirect:/users/table";
    }
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateUser(@PathVariable UUID id) {
        // Get user by id
        // Make isActive = false

        return "redirect:/admin/users";
    }




}
