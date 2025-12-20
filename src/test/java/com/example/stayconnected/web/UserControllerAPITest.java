package com.example.stayconnected.web;

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
import com.example.stayconnected.utils.RevenueUtils;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.controller.UserController;
import com.example.stayconnected.web.dto.user.ChangePasswordRequest;
import com.example.stayconnected.web.dto.user.FilterUserRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import com.example.stayconnected.web.dto.user.UpdatePhotoRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

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
public class UserControllerAPITest {

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

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .wallet(wallet)
                .build();

        when(userService.getUserById(any())).thenReturn(user);

        MockHttpServletRequestBuilder request =
                get("/users/{id}/profile", user.getId())
                        .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("updatePhotoRequest"))
                .andExpect(model().attribute("user", user));

        verify(userService).getUserById(eq(user.getId()));
    }


    @Test
    void whenGetChangePasswordPage_thenReturnChangePasswordViewAndModel() throws Exception {
        User user = randomUser();

        UserPrincipal userPrincipal = new UserPrincipal(
               user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
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
    void patchRequestToChangePassword_WhenInvalid_ShouldReturnSamePage_WithErrors() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder().username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .id(userPrincipal.getId())
                .build();

        user.setWallet(createRandomWallet(user));



        ChangePasswordRequest dto = ChangePasswordRequest.builder()
                .newPassword("Invalid")
                .confirmPassword("InvalidPassword")
                .build();


        when(userService.getUserById(user.getId())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                patch("/users/change-password")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("newPassword", dto.getNewPassword())
                        .param("confirmPassword", dto.getConfirmPassword());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password-form"))
                .andExpect(model().attributeExists("user"));



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



        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + user.getId() + "/profile"));


        ArgumentCaptor<ProfileEditRequest> captor = ArgumentCaptor.forClass(ProfileEditRequest.class);
        verify(userService).updateProfile(eq(user), captor.capture());

    }

    @Test
    void putRequestToEditProfileWithError_shouldReturnSamePage_andNotInvokeServiceMethod() throws Exception {

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
                .username("no")
                .email("invalid@email")
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



        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-edit-form"))
                .andExpect(model().attributeExists("user"));

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
    void sendRequestToProfileEditPage_shouldReturn200Ok_andView() throws Exception {
        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        when(userService.getUserById(any())).thenReturn(user);

        MockHttpServletRequestBuilder request =
                get("/users/profile/edit")
                        .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-edit-form"))
                .andExpect(model().attributeExists("profileEditRequest"));
    }

    @Test
    void sendGetRequestToProfilePageOfUser_shouldReturn200Ok_andView() throws Exception {
        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));

        when(userService.getUserById(any())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                get("/users/{id}/profile",  user.getId())
                        .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-details"));

    }

    @Test
    void sendPatchRequestToProfileEditPhotoPage_shouldReturnRedirect_andView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .profilePictureUrl("uploads/some_url.jpg")
                .build();

        user.setWallet(createRandomWallet(user));


        UpdatePhotoRequest dto =
                new UpdatePhotoRequest("https://cdn-icons-png.flaticon.com/512/25/25471.png");


        when(userService.getUserById(any())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                patch("/users/update-photo")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("photoURL", dto.getPhotoURL());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" +  user.getId() + "/profile"));

        ArgumentCaptor<UpdatePhotoRequest> captor = ArgumentCaptor.forClass(UpdatePhotoRequest.class);

        verify(userService).updatePhoto(eq(user), captor.capture());

        UpdatePhotoRequest actual = captor.getValue();

        assertEquals(dto.getPhotoURL(), actual.getPhotoURL());
    }


    @Test
    void sendPatchRequestToProfileEditPhotoPageWithError_shouldReturnOK_andView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .profilePictureUrl("uploads/some_url.jpg")
                .build();

        user.setWallet(createRandomWallet(user));


        UpdatePhotoRequest dto =
                new UpdatePhotoRequest("invalidUrl");


        when(userService.getUserById(any())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                patch("/users/update-photo")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("photoURL", dto.getPhotoURL());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile-details"))
                .andExpect(model().attributeExists("user"));
    }



    @Test
    void sendGetRequestToUsersTable_shouldReturn200Ok_andView() throws Exception {

        UserPrincipal adminAuthentication = getAdminAuthentication();

        User user = User.builder()
                .id(adminAuthentication.getId())
                .username(adminAuthentication.getUsername())
                .password(adminAuthentication.getPassword())
                .build();


        User user2 = User.builder()
                        .id(UUID.randomUUID())
                                .username("username")
                                        .password("password")
                                            .role(UserRole.USER)
                                                .build();


        user.setWallet(createRandomWallet(user));
        user2.setWallet(createRandomWallet(user2));

        FilterUserRequest dto = new FilterUserRequest("ALL", "ALL");


        when(userService.getFilteredUsers(any()))
                .thenReturn(List.of(user, user2));

        when(userService.getUserById(any())).thenReturn(user);


        MockHttpServletRequestBuilder request =
                get("/users/table/filter")
                        .with(user(adminAuthentication));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/users"))
                .andExpect(model().attribute("authUser", user))
                .andExpect(model().attributeExists("filterUsersRequest"))
                .andExpect(model().attributeExists("users"));

        verify(userService).getFilteredUsers(any());
    }


    @Test
    void sendGetRequestToAppStatsPage_shouldReturn200Ok_andView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));


        when(userService.getUserById(any())).thenReturn(user);
        when(userService.getAllUsersOrderedByDateAndUsername()).thenReturn(List.of(user));
        when(propertyService.getAllProperties()).thenReturn(List.of());
        when(transactionService.getAllTransactions()).thenReturn(List.of());
        when(transactionService.getTotalRevenue()).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getAllFailedTransactions()).thenReturn(List.of());
        when(userService.getTotalActiveUsers()).thenReturn(1L);
        when(reservationService.getTotalReservationsByStatus("ALL")).thenReturn(0L);
        when(transactionService.getAverageTransactionAmount()).thenReturn(BigDecimal.valueOf(100));
        when(dashboardStatsService.getCountNewUsersToday()).thenReturn(2L);
        when(dashboardStatsService.getCountNewReservationsToday()).thenReturn(3L);
        when(dashboardStatsService.getCountTotalRevenueToday()).thenReturn(BigDecimal.valueOf(500));
        when(dashboardStatsService.getCountNewPropertiesToday()).thenReturn(1L);
        when(reservationService.getTotalReservationsByStatus("BOOKED")).thenReturn(0L);
        when(userService.getPercentageActiveUsers()).thenReturn(BigDecimal.valueOf(50));
        when(reservationService.getAveragePercentageOfReservationsByStatus("BOOKED")).thenReturn(BigDecimal.ZERO);
        when(dashboardStatsService.getAverageWeeklyTransactionGrowth()).thenReturn(BigDecimal.valueOf(10));


        MockHttpServletRequestBuilder request =
                get("/users/app-stats")
                        .with(user(userPrincipal));



        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("admin/stats"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("totalUsers", 1))
                .andExpect(model().attribute("totalProperties", 0))
                .andExpect(model().attribute("totalTransactions", 0))
                .andExpect(model().attribute("totalRevenue", RevenueUtils.formatRevenue(BigDecimal.valueOf(1000))))
                .andExpect(model().attribute("totalFailedTransactions", 0))
                .andExpect(model().attribute("totalActiveUsers", 1L))
                .andExpect(model().attribute("totalReservations", 0L))
                .andExpect(model().attribute("averageTransactionAmount", BigDecimal.valueOf(100)))
                .andExpect(model().attribute("newUsersToday", 2L))
                .andExpect(model().attribute("newBookingsToday", 3L))
                .andExpect(model().attribute("totalRevenueToday", RevenueUtils.formatRevenue(BigDecimal.valueOf(500))))
                .andExpect(model().attribute("newPropertiesToday", 1L))
                .andExpect(model().attribute("totalBookedReservations", 0L))
                .andExpect(model().attribute("percentageActiveUsers", BigDecimal.valueOf(50)))
                .andExpect(model().attribute("percentageBookedReservations", BigDecimal.valueOf(0)))
                .andExpect(model().attribute("averageTransactionGrowth", BigDecimal.valueOf(10)));

    }








    public static UserPrincipal getAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                "zhekogyulev@gmail.com",
                true,
                UserRole.ADMIN
        );
    }

    public static UserPrincipal getNonAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                "zhekogyulev@gmail.com",
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

    public static Wallet createRandomWallet(User user) {

        return Wallet.builder().id(UUID.randomUUID())
                .owner(user)
                .build();
    }
}