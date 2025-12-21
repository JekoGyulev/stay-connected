package com.example.stayconnected.user.service;


import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.user.*;
import org.springframework.data.domain.Page;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    Page<User> getAllUsersOrderedByDateAndUsername(int pageNumber, int pageSize);

    User getUserById(UUID id);

    void updateProfile(User user, ProfileEditRequest profileEditRequest);

    void switchRole(UUID userId);

    void switchStatus(UUID userId);

    long getTotalActiveUsers();

    void saveUser(User user);

    void updatePhoto(User user, UpdatePhotoRequest updatePhotoRequest);

    void changePassword(User user, ChangePasswordRequest changePasswordRequest);

    BigDecimal getPercentageActiveUsers();

    Page<User> getFilteredUsers(int pageNumber, int pageSize, FilterUserRequest filterUserRequest);

    List<User> getUsersBySearchUsername(String username);

    User getUserByEmail(String email);

    long getAllUsers();
}
