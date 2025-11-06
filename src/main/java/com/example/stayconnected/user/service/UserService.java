package com.example.stayconnected.user.service;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.user.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    List<User> getAllUsers();

    User getUserById(UUID id);

    void updateProfile(User user, ProfileEditRequest profileEditRequest);

    void switchRole(UUID userId);

    void switchStatus(UUID userId);

    long getTotalInactiveUsers();

    long getTotalActiveUsers();

    void saveUser(User user);

    void updatePhoto(User user, UpdatePhotoRequest updatePhotoRequest);

    void changePassword(User user, ChangePasswordRequest changePasswordRequest);

    // View stats
}
