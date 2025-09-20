package com.example.stayconnected.user.service;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.LoginRequest;
import com.example.stayconnected.web.dto.RegisterRequest;

public interface UserService {

    User register(RegisterRequest request);
    User login(LoginRequest request);

    // Change user's role
    // View stats
}
