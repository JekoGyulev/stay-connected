package com.example.stayconnected.user.service;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.user.LoginRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import com.example.stayconnected.web.dto.user.RegisterRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    List<User> getAllUsers();

    User login(LoginRequest request);

    User getUserById(UUID id);

    void updateProfile(User user, ProfileEditRequest profileEditRequest);

    // Change user's role
    void switchRole(UUID userId);

    // View stats
}
