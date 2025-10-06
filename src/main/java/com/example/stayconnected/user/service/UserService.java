package com.example.stayconnected.user.service;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.user.LoginRequest;
import com.example.stayconnected.web.dto.user.RegisterRequest;

import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);
    User login(LoginRequest request);

    User getUserById(UUID id);

    // Change user's role
    // View stats
}
