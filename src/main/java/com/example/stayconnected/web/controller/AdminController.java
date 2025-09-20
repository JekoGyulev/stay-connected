package com.example.stayconnected.web.controller;

import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getUsersTable() {

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

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserRole(@PathVariable UUID id) {
        // Get user by id
        // Change the role of the user

        return "redirect:/admin/users";
    }
    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateUser(@PathVariable UUID id) {
        // Get user by id
        // Make isActive = false

        return "redirect:/admin/users";
    }

}
