package com.example.stayconnected.security.oauth2;

import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Autowired
    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService =
                new DefaultOAuth2UserService();

        OAuth2User oAuth2User = customOAuth2UserService.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isEmpty()) {

            String token = userRequest.getAccessToken().getTokenValue();


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    (Class<List<Map<String, Object>>>) (Class<?>) List.class
            );

            List<Map<String, Object>> emails = response.getBody();

            if (emails != null) {
                for (Map<String, Object> e : emails) {
                    Boolean primary = (Boolean) e.get("primary");
                    Boolean verified = (Boolean) e.get("verified");

                    // Gets the primary and verified email of user
                    if (primary != null && primary && verified != null && verified) {
                        email = (String) e.get("email");
                        break;
                    }
                }
            }

            if (email == null) {
                throw new OAuth2AuthenticationException("Cannot fetch GitHub email");
            }
        }


        User user = this.userService.getUserByEmail(email);

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isActive(),
                user.getRole());
    }
}
