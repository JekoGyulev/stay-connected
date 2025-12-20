package com.example.stayconnected.security.oauth2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("github")) {
            String email = oAuth2User.getAttribute("email");


            if (email == null) {
                String token = userRequest.getAccessToken().getTokenValue();
                email = fetchGitHubEmail(token);
            }

            Map<String, Object> enrichedAttributes = new HashMap<>(oAuth2User.getAttributes());

            enrichedAttributes.put("email", email);

            return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    enrichedAttributes,
                    "id"
            );

        }

        return oAuth2User;
    }

    @SuppressWarnings("unchecked")
    private static String fetchGitHubEmail(String token) {
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

        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary"))
                        && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException("GitHub email not found"));
    }
}
