package com.example.stayconnected.web.controller;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public ModelAndView modelAndView(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = this.userService.getUserById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/home");
        modelAndView.addObject("user", user);

        // Later : add appropriate objects (e.g user....)

        return modelAndView;
    }


}
