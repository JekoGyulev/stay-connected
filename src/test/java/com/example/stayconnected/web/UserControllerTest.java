package com.example.stayconnected.web;

import com.example.stayconnected.config.WebConfiguration;
import com.example.stayconnected.dashboard.DashboardStatsService;
import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.controller.UserController;
import com.example.stayconnected.web.dto.user.ChangePasswordRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PropertyService propertyService;
    @MockitoBean
    private WalletService walletService;
    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean
    private ReservationService reservationService;
    @MockitoBean
    private DashboardStatsService dashboardStatsService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetUserProfile_thenReturnProfileViewAndModel() throws Exception {

        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(50)).build();

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("john")
                .email("john@example.com")
                .wallet(wallet)
                .build();

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}/profile", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("updatePhotoRequest"))
                .andExpect(model().attribute("user", user));

        verify(userService).getUserById(userId);
    }


    @Test
    void whenGetChangePasswordPage_thenReturnChangePasswordViewAndModel() throws Exception {
        User user = randomUser();

        UserPrincipal userPrincipal = new UserPrincipal(
               user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                user.getRole()
        );

        when(userService.getUserById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/users/change-password")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password-form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("changePasswordRequest"));
    }

    @Test
    void patchRequestToChangeUserPassword_shouldReturnRedirect_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userService.getUserById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder request = patch("/users/change-password")
                .with(user(userPrincipal))
                .with(csrf())
                .param("newPassword", changePasswordRequest.getNewPassword())
                .param("confirmPassword", changePasswordRequest.getConfirmPassword());



        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + user.getId() + "/profile"));

        verify(userService).changePassword(eq(user), any(ChangePasswordRequest.class));


    }



    @Test
    void patchRequestToChangeUserStatus_fromAdminUser_shouldReturnRedirect_andInvokeServiceMethod() throws Exception {
        MockHttpServletRequestBuilder request =
                patch("/users/{id}/status", UUID.randomUUID())
                        .with(user(getAdminAuthentication()))
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/table"));

        verify(userService).switchStatus(any());

    }

    @Test
    void putRequestToEditProfile_shouldReturnRedirect_andInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .email("zhekogyulev@gmail.com")
                .build();

        Wallet wallet = Wallet.builder().balance(BigDecimal.TEN).build();

        user.setWallet(wallet);

        ProfileEditRequest profileEditRequest = ProfileEditRequest.builder()
                .username("newUsername")
                .email("newemail@example.com")
                .firstName("newFirstName")
                .lastName("newLastName")
                .build();


        when(userService.getUserById(userPrincipal.getId())).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/profile/edit")
                .with(user(userPrincipal))
                .with(csrf())
                .param("username", profileEditRequest.getUsername())
                .param("email", profileEditRequest.getEmail())
                .param("firstName", profileEditRequest.getFirstName())
                .param("lastName", profileEditRequest.getLastName());


        // Act
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + user.getId() + "/profile"));

        // Assert: verify service method call using ArgumentCaptor
        ArgumentCaptor<ProfileEditRequest> captor = ArgumentCaptor.forClass(ProfileEditRequest.class);
        verify(userService).updateProfile(eq(user), captor.capture());

    }





    @Test
    void patchRequestToChangeUserRole_fromAdminUser_shouldReturnRedirect_andInvokeServiceMethod() throws Exception {
        MockHttpServletRequestBuilder request =
                patch("/users/{id}/role", UUID.randomUUID())
                        .with(user(getAdminAuthentication()))
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/table"));

        verify(userService).switchRole(any());
    }

    @Test
    void getUsersTablePage_shouldReturn200AndView() throws Exception {

        UserPrincipal admin = getAdminAuthentication();
        User user = new User();
        Wallet wallet =  Wallet.builder().balance(BigDecimal.valueOf(50)).build();
        user.setId(admin.getId());
        user.setUsername(admin.getUsername());
        user.setWallet(wallet);

        List<User> users = List.of(user);

        when(userService.getAllUsersOrderedByDateAndUsername()).thenReturn(users);
        when(userService.getUserById(admin.getId())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                get("/users/table")
                        .with(user(admin));

        mockMvc.perform(request)
                .andExpect(view().name("admin/users"))
                .andExpect(status().isOk())
                        .andExpect(model().attributeExists("users"))
                                .andExpect(model().attributeExists("filterUsersRequest"))
                                        .andExpect(model().attributeExists("authUser"));


    }


    @Test
    void getWalletPage_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();


        Wallet wallet =  Wallet.builder().balance(BigDecimal.valueOf(50)).build();

        User user = new User();
        user.setWallet(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .owner(user)
                .build();

        when(userService.getUserById(any())).thenReturn(user);
        when(walletService.getLastThreeTransactions(any())).thenReturn(List.of(transaction));

        MockHttpServletRequestBuilder request =
                get("/users/wallet")
                        .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("wallet/user-wallet"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("wallet"))
                .andExpect(model().attributeExists("transactions"));
    }






    public static UserPrincipal getAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                true,
                UserRole.ADMIN
        );
    }

    public static UserPrincipal getNonAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                true,
                UserRole.USER
        );
    }

    public static User randomUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("Jeko777")
                .password("Jeko123123")
                .isActive(true)
                .role(UserRole.USER)
                .firstName("Jeko")
                .lastName("Gyulev")
                .registeredAt(LocalDateTime.now())
                .build();

        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).owner(user).createdAt(LocalDateTime.now())
                .balance(BigDecimal.TEN)
                .build();

        user.setWallet(wallet);

        return user;
    }





//    @Test
//    void getWalletPage_ShouldReturnWalletViewWithModel() throws Exception {
//
//        // --- Arrange ---
//        UUID userId = UUID.randomUUID();
//
//        UserPrincipal principal = new UserPrincipal(
//                userId,
//                "john",
//                "hashed-password",
//                true,
//                UserRole.USER
//        );
//
//        Wallet wallet = Wallet.builder()
//                .id(UUID.randomUUID())
//                .balance(BigDecimal.valueOf(100))
//                .build();
//
//        User user = User.builder()
//                .id(userId)
//                .username("john")
//                .wallet(wallet)
//                .build();
//
//        List<Transaction> transactions = List.of(
//                new Transaction(),
//                new Transaction(),
//                new Transaction()
//        );
//
//        Mockito.when(userService.getUserById(userId)).thenReturn(user);
//        Mockito.when(walletService.getLastThreeTransactions(wallet)).thenReturn(transactions);
//
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(
//                        principal,
//                        principal.getPassword(),
//                        principal.getAuthorities()
//                );
//
//        // --- Act + Assert ---
//        mockMvc.perform(get("/wallet")
//                        .with(authentication(auth)))     // <-- NEED THIS FOR @WebMvcTest
//                .andExpect(status().isOk())
//                .andExpect(view().name("wallet/user-wallet"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("wallet"))
//                .andExpect(model().attributeExists("transactions"));
//    }


}