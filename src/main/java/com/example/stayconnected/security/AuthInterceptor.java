package com.example.stayconnected.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of("/register", "/login");

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

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
