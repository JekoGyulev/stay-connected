package com.example.stayconnected.web.controller;

import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public String redirectToLoginPage(@Valid @ModelAttribute RegisterRequest request,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        this.userService.register(request);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }


    // POST /auth/logout -> Logout user
}
