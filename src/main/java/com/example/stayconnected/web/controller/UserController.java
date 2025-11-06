package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.user.ChangePasswordRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import com.example.stayconnected.web.dto.user.UpdatePhotoRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
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
        modelAndView.addObject("updatePhotoRequest", new UpdatePhotoRequest());

        return modelAndView;
    }
    @PatchMapping("/update-photo")
    public ModelAndView updatePhoto(@Valid UpdatePhotoRequest updatePhotoRequest,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserPrincipal principal) {

        // TODO: FIX HERE the PRINCIPAL

        User user = this.userService.getUserById(principal.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("user/profile-details");
            modelAndView.addObject("user", user);
            return modelAndView;
        }


        this.userService.updatePhoto(user, updatePhotoRequest);
        return new ModelAndView("redirect:/users/" + user.getId() + "/profile");
    }



    @GetMapping("/profile/edit")
    public ModelAndView getEditProfilePage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = this.userService.getUserById(userPrincipal.getId());

        ProfileEditRequest profileEditRequest = DtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-edit-form");
        modelAndView.addObject("profileEditRequest", profileEditRequest);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
    @PutMapping("/profile/edit")
    public ModelAndView editProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @Valid @ModelAttribute ProfileEditRequest profileEditRequest,
                                    BindingResult bindingResult
                                    ) {

        User user = this.userService.getUserById(userPrincipal.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("user/profile-edit-form");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        this.userService.updateProfile(user, profileEditRequest);

        return new ModelAndView("redirect:/users/" + user.getId() + "/profile");
    }

    @GetMapping("/change-password")
    public ModelAndView showChangePasswordPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = this.userService.getUserById(userPrincipal.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/change-password-form");
        modelAndView.addObject("user", user);
        modelAndView.addObject("changePasswordRequest", new ChangePasswordRequest());

        return modelAndView;
    }

    @PatchMapping("/change-password")
    public ModelAndView changePassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = this.userService.getUserById(userPrincipal.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("user/change-password-form");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        this.userService.changePassword(user, changePasswordRequest);

        return new ModelAndView("redirect:/users/" + user.getId() + "/profile");
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
