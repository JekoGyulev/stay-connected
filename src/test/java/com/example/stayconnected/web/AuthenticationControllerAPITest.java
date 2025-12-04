package com.example.stayconnected.web;


import com.example.stayconnected.config.WebConfiguration;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.controller.AuthController;
import com.example.stayconnected.web.dto.user.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfiguration.class))
public class AuthenticationControllerAPITest {

    @MockitoBean
    private  UserService userService;
    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRegisterPage_shouldReturn200OkAndView() throws Exception {

        mockMvc.perform(get("/auth/register"))
                .andExpect(view().name("register"))
                .andExpect(status().isOk());
    }

    @Test
    void postRegister_shouldReturn200OkAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {
        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("firstName", "firstName")
                .formField("lastName", "lastName")
                .formField("email", "zhekogyulev@gmail.com")
                .formField("username", "Jeko777")
                .formField("password", "Password123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("successfulRegistration"));;

        verify(userService).register(registerRequestArgumentCaptor.capture());
        RegisterRequest registerRequest = registerRequestArgumentCaptor.getValue();
        assertEquals("Jeko777", registerRequest.getUsername());
        assertEquals("Password123", registerRequest.getPassword());
    }

    @Test
    void postRegister_shouldReturn200OkAndShowRegisterViewAndInvokeRegisterServiceMethodIsNeverInvolved() throws Exception {
        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("firstName", "firstName")
                .formField("lastName", "lastName")
                .formField("email", "1")
                .formField("username", "2")
                .formField("password", "password123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }

    @Test
    void getLoginPage_shouldReturn200AndView() throws Exception {

        MockHttpServletRequestBuilder request = get("/auth/login");

        mockMvc.perform(request)
                .andExpect(view().name("login"))
                .andExpect(status().isOk());
    }

    @Test
    void getLoginPage_withError_shouldReturn200AndViewWithErrorMessage() throws Exception {
        MockHttpServletRequestBuilder request = get("/auth/login");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("error", "Invalid credentials");

        mockMvc.perform(request.session(session))
                .andExpect(view().name("login"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("errorMessage", "Invalid credentials"));
    }
























}
