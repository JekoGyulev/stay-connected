package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.service.PropertyService;
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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public UserController(ReservationService reservationService, UserService userService, PropertyService propertyService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getUserProfilePage(@PathVariable UUID id) {
        User user = this.userService.getUserById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-details");
        modelAndView.addObject("user", user);

        // Will be used to show the ID, profilePicture, username, age, first- and lastname, registered,lastLoggedIn of user, email

        return modelAndView;
    }

    @GetMapping("/{id}/profile/edit")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {
        User user = this.userService.getUserById(id);

        ProfileEditRequest profileEditRequest = DtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-edit-form");
        modelAndView.addObject("profileEditRequest", profileEditRequest);

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
        List<User> users = this.userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @GetMapping("/app-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getStatsPage() {
        // Fetch stats for : total bookings made (look at smart wallet's way of stats page)

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/stats");
        modelAndView.addObject("totalUsers", this.userService.getAllUsers().size());
        modelAndView.addObject("totalInactiveUsers", this.userService.getTotalInactiveUsers());
        modelAndView.addObject("totalActiveUsers", this.userService.getTotalActiveUsers());
        modelAndView.addObject("totalProperties", this.propertyService.getAllProperties().size());

        // TODO: Add later total bookings

        return modelAndView;
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserRole(@PathVariable UUID id) {
        this.userService.switchRole(id);
        return "redirect:/users/table";
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserStatus(@PathVariable UUID id) {
        this.userService.switchStatus(id);
        return "redirect:/users/table";
    }




}
