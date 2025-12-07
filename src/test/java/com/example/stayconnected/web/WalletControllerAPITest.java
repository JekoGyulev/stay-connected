package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.controller.WalletController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.util.diff.DiffUtils.patch;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(WalletController.class)
public class WalletControllerAPITest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private WalletService walletService;
    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    @Autowired
    MockMvc mockMvc;

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
                get("/wallet")
                        .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("wallet/user-wallet"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("wallet"))
                .andExpect(model().attributeExists("transactions"));
    }


    @Test
    void makeTopUpOperationToWallet_shouldReturnToTransactionAndIncreaseBalanceOfWallet() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .owner(user)
                .receiver(user.getWallet().getId().toString())
                .amount(BigDecimal.valueOf(20))
                .build();



        when(userService.getUserById(any())).thenReturn(user);

        when(walletService.topUp(any(), any(), any()))
                .thenReturn(transaction);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.patch("/wallet/top-up")
                        .param("amount", "20")
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions/" + transaction.getId()));


        verify(userService).getUserById(any());
        verify(walletService).topUp(any(), any(), any());

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

    public static Wallet createRandomWallet(User user) {

        return Wallet.builder().id(UUID.randomUUID())
                .owner(user)
                .build();
    }




}
