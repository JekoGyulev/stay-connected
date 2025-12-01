package com.example.stayconnected.web.controller;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final UserService userService;

    @Autowired
    public ReservationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/table")
    public ModelAndView getReservationsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());
        // Get user's reservations


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation/user-reservations");
        modelAndView.addObject("user", user);



        return modelAndView;
    }

}
