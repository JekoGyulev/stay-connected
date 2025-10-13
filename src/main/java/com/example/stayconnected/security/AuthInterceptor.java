package com.example.stayconnected.security;


import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String LOGIN_FAILED_INACTIVE_PROFILE_MESSAGE = "Your account is inactive. Please contact support";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of("/register", "/login");

    private final UserService userService;

    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getServletPath();

        if (path.equals("/")) return true;

        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (path.startsWith(endpoint)) return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        UUID userId = (UUID) session.getAttribute("userId");

        if (userId == null) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }

        User user = this.userService.getUserById(userId);

        if (!user.isActive()) {
            session.invalidate();
            response.sendRedirect("/login?loginMessageProfileInactive=" +
                    LOGIN_FAILED_INACTIVE_PROFILE_MESSAGE);
            return false;
        }


        return true;
    }
}
