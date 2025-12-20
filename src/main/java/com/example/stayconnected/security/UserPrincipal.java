package com.example.stayconnected.security;

import com.example.stayconnected.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User, OidcUser {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private boolean isActive;
    private UserRole role;
    private Map<String, Object> attributes;

    public UserPrincipal(UUID id, String username, String password, String email, boolean isActive, UserRole role) {
        this(id, username, password, email, isActive, role, Map.of());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority("ROLE_" + this.role.name());

        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return Map.of();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}
