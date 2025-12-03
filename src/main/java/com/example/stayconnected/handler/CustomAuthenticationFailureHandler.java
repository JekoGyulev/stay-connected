package com.example.stayconnected.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        System.out.println("Failure handler triggered Exception: " + exception.getClass().getSimpleName());

        if (exception instanceof DisabledException) {
            request.getSession().setAttribute("error", "Your account has been disabled");
        } else if (exception instanceof BadCredentialsException) {
            request.getSession().setAttribute("error", "Username or password is incorrect");
        }

        response.sendRedirect("/auth/login");
    }
}
