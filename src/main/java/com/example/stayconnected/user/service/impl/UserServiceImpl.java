package com.example.stayconnected.user.service.impl;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utility.exception.EmailAlreadyExists;
import com.example.stayconnected.utility.exception.LoginFailed;
import com.example.stayconnected.utility.exception.UserInactive;
import com.example.stayconnected.utility.exception.UsernameAlreadyExists;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.dto.user.LoginRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import com.example.stayconnected.web.dto.user.RegisterRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final WalletService walletService;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User register(RegisterRequest request) {

        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists("Username already taken: %s"
                    .formatted(request.getUsername()));
        }

        User user = initUser(request);

        this.userRepository.save(user);

        Wallet wallet = this.walletService.createWallet(user);

        user.setWallet(wallet);

        log.info("Successfully registered user with id [%s] and username [%s]"
                .formatted(user.getId(), user.getUsername()));

        SuccessfulRegistrationEvent event = SuccessfulRegistrationEvent
                .builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        this.eventPublisher.publishEvent(event);

        return user;
    }

    @Override
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User login(LoginRequest request) {

        Optional<User> optionalUser = this.userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new LoginFailed("Username or password is incorrect");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailed("Username or password is incorrect");
        }

        user.setLastLoggedIn(LocalDateTime.now());

        log.info("Successfully logged user in with id [%s] and username [%s]".formatted(user.getId(), user.getUsername()));

        return this.userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void updateProfile(User user, ProfileEditRequest profileEditRequest) {
        user.setFirstName(profileEditRequest.getFirstName());
        user.setLastName(profileEditRequest.getLastName());
        user.setAge(profileEditRequest.getAge());
        user.setProfilePictureUrl(profileEditRequest.getProfilePicture());
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
    public long getTotalInactiveUsers() {
        return this.userRepository.countAllByActiveIs(false);
    }

    @Override
    public long getTotalActiveUsers() {
        return this.userRepository.countAllByActiveIs(true);
    }


    private User initUser(RegisterRequest request) {
        return new User (
                request.getFirstName(),
                request.getLastName(),
                request.getAge(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
        );
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
