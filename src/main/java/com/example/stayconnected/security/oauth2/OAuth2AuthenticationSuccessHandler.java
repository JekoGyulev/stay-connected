package com.example.stayconnected.security.oauth2;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
        setDefaultTargetUrl("/home");
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        User user = this.userService.getUserByEmail(email);

        UserPrincipal userPrincipal = new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.isActive(),
            user.getRole()
        );

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        super.onAuthenticationSuccess(request, response, newAuthentication);
    }



}
