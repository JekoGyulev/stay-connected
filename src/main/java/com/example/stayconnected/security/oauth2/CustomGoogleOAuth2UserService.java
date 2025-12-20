package com.example.stayconnected.security.oauth2;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class CustomGoogleOAuth2UserService extends OidcUserService {

    private final UserService userService;

    @Autowired
    public CustomGoogleOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from Google account");
        }

        User user = this.userService.getUserByEmail(email);

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isActive(),
                user.getRole()
        );

    }



}
