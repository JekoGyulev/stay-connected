package com.example.stayconnected.authentication;


import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(value = AuthController.class)
public class AuthenticationControllerTest {

    @MockitoBean
    private  UserService userService;


    @Test
    void whenGetUserProfile_thenReturnProfileViewAndModel() throws Exception{

    }

}
