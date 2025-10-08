package com.example.stayconnected.web.controller;

import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
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

        return modelAndView;
    }
    @PutMapping("/{id}/profile/edit")
    public ModelAndView editProfile(@PathVariable UUID id,
                                    @Valid @ModelAttribute ProfileEditRequest profileEditRequest,
                                    BindingResult bindingResult
                                    ) {

        User user = this.userService.getUserById(id);

        if (bindingResult.hasErrors()) {
            return new ModelAndView("user/profile-edit-form");
        }

        //TODO: implement functionality updateProfile(User user, ProfileEditRequest request)
        //this.userService.updateProfile(user, profileEditRequest);

        return new ModelAndView("redirect:/users/" + id + "/profile");
    }




}
