package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.controller.ReviewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
public class ReviewControllerAPITest {

    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PropertyService propertyService;
    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    MockMvc mockMvc;







}
