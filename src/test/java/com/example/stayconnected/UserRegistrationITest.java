package com.example.stayconnected;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utils.exception.UserDoesNotExist;
import com.example.stayconnected.web.dto.user.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRegistrationITest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void whenUserRegisters_shouldBeInvokedServiceMethod_andSavedToDatabase() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("John")
                .email("John@gmail.com")
                .password("John1234562")
                .username("John16")
                .build();

        User registeredUser = userService.register(registerRequest);

        User savedUser = userRepository.findById(registeredUser.getId())
                .orElseThrow(() -> new UserDoesNotExist("User doesn't exist"));

        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        assertEquals("John@gmail.com", savedUser.getEmail());
        assertNotEquals("John123456", savedUser.getPassword());

        assertNotNull(savedUser.getWallet());
        assertEquals(savedUser.getId(), savedUser.getWallet().getOwner().getId());
    }





}
