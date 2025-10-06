package com.example.stayconnected.user.service.impl;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utility.exception.LoginFailed;
import com.example.stayconnected.utility.exception.UserInactive;
import com.example.stayconnected.utility.exception.UsernameAlreadyExists;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.dto.user.LoginRequest;
import com.example.stayconnected.web.dto.user.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final WalletService walletService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(RegisterRequest request) {

        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists("Username already taken: %s"
                    .formatted(request.getUsername()));
        }

        // Create an account
        User user = initUser(request);

        // Save user so that the id is generated (which we will need for setting the wallet)
        this.userRepository.save(user);

        // Assign a wallet to the user
        Wallet wallet = this.walletService.createWallet(user);

        user.setWallet(wallet);

        log.info("Successfully registered user with id [%s] and username [%s]"
                .formatted(user.getId(), user.getUsername()));

        return user;
    }

    @Override
    public User login(LoginRequest request) {

        Optional<User> optionalUser = this.userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new LoginFailed("Username or password is incorrect");
        }

        User user = optionalUser.get();

        if (!user.isActive()) {
            throw new UserInactive("Your account is deactivated. Please contact support");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailed("Username or password is incorrect");
        }

        user.setLastLoggedIn(LocalDateTime.now());

        log.info("Successfully logged user in with id [%s] and username [%s]".formatted(user.getId(), user.getUsername()));

        return this.userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
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
}
