package com.example.stayconnected.web.controller;

import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Shows user profile details

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-menu");

        return modelAndView;
    }

    @GetMapping("/{id}/profile/edit")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/profile-edit");

        // Get the user by id
        // Create an object of ProfileEditRequest class
        // Set the dto properties to user's properties
        // Then add the dto as an object to the ModelAndView (since we will use Thymeleaf)

        return modelAndView;
    }
    @PutMapping("/{id}/profile/edit")
    public ModelAndView editProfile(@PathVariable UUID id,
                                    @Valid @ModelAttribute ProfileEditRequest profileEditRequest,
                                    BindingResult bindingResult
                                    ) {
        ModelAndView modelAndView = new ModelAndView();

        // Check if there is any errors (which mean validation failed) with bindingResult.hasErrros();

        // IF there are -> set view "user/profile-edit" and return the ModelAndView

        /* IF NOT:
              Then call a method from userService that updates user's credentials and
              accepts as a parameter the UserEditRequest.

              Then redirect to "/users/{id}/profile"
         */


        return modelAndView;
    }




}
