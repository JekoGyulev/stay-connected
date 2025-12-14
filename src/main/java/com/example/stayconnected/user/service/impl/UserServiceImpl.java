package com.example.stayconnected.user.service.impl;

import com.example.stayconnected.event.UserRegisteredEventPublisher;
import com.example.stayconnected.event.payload.UserRegisteredEvent;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utils.exception.EmailAlreadyExists;
import com.example.stayconnected.utils.exception.UserDoesNotExist;
import com.example.stayconnected.utils.exception.UsernameAlreadyExists;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.dto.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final WalletService walletService;

    private final PasswordEncoder passwordEncoder;

    private final UserRegisteredEventPublisher userRegisteredEventPublisher;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, PasswordEncoder passwordEncoder, UserRegisteredEventPublisher userRegisteredEventPublisher) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
        this.userRegisteredEventPublisher = userRegisteredEventPublisher;
    }


    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User register(RegisterRequest request) {

        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists("Username already taken: %s"
                    .formatted(request.getUsername()));
        }

        if (this.userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExists("Email already taken: %s"
                    .formatted(request.getEmail()));
        }

        User user = initUser(request);

        this.userRepository.save(user);

        Wallet wallet = this.walletService.createWallet(user);

        user.setWallet(wallet);

        log.info("Successfully registered user with id [%s] and username [%s]"
                .formatted(user.getId(), user.getUsername()));


        UserRegisteredEvent event = UserRegisteredEvent
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        this.userRegisteredEventPublisher.publish(event);

        return user;
    }

    @Override
    @Cacheable(value = "users")
    public List<User> getAllUsersOrderedByDateAndUsername() {
        return this.userRepository.findAllByOrderByRegisteredAtDescUsernameAsc();
    }

    @Override
    public User getUserById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExist("User not found"));
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void updateProfile(User user, ProfileEditRequest profileEditRequest) {
        user.setFirstName(profileEditRequest.getFirstName());
        user.setLastName(profileEditRequest.getLastName());
        user.setUsername(profileEditRequest.getUsername());
        user.setEmail(profileEditRequest.getEmail());

        this.userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getUserById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        this.userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {
        User user = getUserById(userId);
        user.setActive(!user.isActive());
        this.userRepository.save(user);
    }


    @Override
    public long getTotalActiveUsers() {
        return this.userRepository.countAllByActiveIs(true);
    }

    @Override
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void updatePhoto(User user, UpdatePhotoRequest updatePhotoRequest) {
        user.setProfilePictureUrl(updatePhotoRequest.getPhotoURL());
        this.userRepository.save(user);
    }

    @Override
    public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        this.userRepository.save(user);
    }

    @Override
    public BigDecimal getPercentageActiveUsers() {

        long totalUsers = getAllUsersOrderedByDateAndUsername().size();

        if (totalUsers == 0) {
            return BigDecimal.ZERO;
        }

        long totalActiveUsers = getTotalActiveUsers();

        return BigDecimal.valueOf((totalActiveUsers * 100.0) / totalUsers)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<User> getFilteredUsers(FilterUserRequest filterUserRequest) {

        String strRole = filterUserRequest.getUserRole();
        String strStatus = filterUserRequest.getUserStatus();

        boolean roleFilter = strRole != null && !strRole.equals("ALL");
        boolean statusFilter = strStatus != null && !strStatus.equals("ALL");

        if (!roleFilter && !statusFilter) {
            return this.getAllUsersOrderedByDateAndUsername();
        }

        if (roleFilter &&  statusFilter) {
            UserRole userRole = UserRole.valueOf(strRole);

            boolean isActive = strStatus.equals("true");

            return this.userRepository.findAllByRoleAndIsActiveOrderByRegisteredAtDescUsernameAsc(
                    userRole,
                    isActive
            );
        }

        if (roleFilter) {
            UserRole userRole = UserRole.valueOf(strRole);
            return this.userRepository.findAllByRoleOrderByRegisteredAtDescUsernameAsc(userRole);
        }


        boolean isActive = strStatus.equals("true");

        return this.userRepository.findAllByIsActiveOrderByRegisteredAtDescUsernameAsc(
            isActive
        );
    }

    private User initUser(RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isActive(true)
                .role(UserRole.USER)
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));


        return new UserPrincipal(
                                    user.getId(),
                                    user.getUsername(),
                                    user.getPassword(),
                                    user.isActive(),
                                    user.getRole()
        );
    }
}
