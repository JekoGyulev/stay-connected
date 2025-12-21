package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.controller.TransactionController;
import com.example.stayconnected.web.dto.transaction.FilterTransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(TransactionController.class)
public class TransactionControllerAPITest {

    @MockitoBean
    private  TransactionService transactionService;
    @MockitoBean
    private  UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


//    @Test
//    void getTransactionsPage_shouldReturn200AndView_withModelAttributes() throws Exception {
//        // Arrange
//        UserPrincipal userPrincipal = getNonAdminAuthentication();
//
//        User user = User.builder()
//                .id(userPrincipal.getId())
//                .username(userPrincipal.getUsername())
//                .password(userPrincipal.getPassword())
//                .build();
//
//
//        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(50)).build();
//
//        user.setWallet(wallet);
//
//        Transaction tx1 = Transaction.builder().id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(100))
//                .type(TransactionType.DEPOSIT)
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//        Transaction tx2 = Transaction.builder().id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(150))
//                .type(TransactionType.DEPOSIT)
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//        List<Transaction> transactions = List.of(tx1, tx2);
//
//        when(userService.getUserById(userPrincipal.getId())).thenReturn(user);
//        when(transactionService.getTransactionsByUserId(user.getId())).thenReturn(transactions);
//
//        // Act & Assert
//        mockMvc.perform(get("/transactions")
//                        .with(user(userPrincipal))) // set authenticated user
//                .andExpect(status().isOk())
//                .andExpect(view().name("/transaction/transactions"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("transactions"))
//                .andExpect(model().attributeExists("filterTransaction"))
//                .andExpect(model().attribute("user", user))
//                .andExpect(model().attribute("transactions", transactions));
//    }

    @Test
    void getTransactionDetails_shouldReturn200AndView_withModelAttributes() throws Exception {
        // Arrange
        UserPrincipal userPrincipal = getNonAdminAuthentication();
        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(50)).build();

        user.setWallet(wallet);

        UUID transactionId = UUID.randomUUID();
        Transaction transaction = Transaction.builder().id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .build();

        when(userService.getUserById(userPrincipal.getId())).thenReturn(user);
        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        // Act & Assert
        mockMvc.perform(get("/transactions/{id}", transactionId)
                        .with(user(userPrincipal))) // set authenticated user
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/transaction-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("transaction"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("transaction", transaction));
    }


//    @Test
//    void showFilteredPage_shouldReturn200AndView_withFilteredTransactions() throws Exception {
//        // Arrange
//        UserPrincipal userPrincipal = getNonAdminAuthentication();
//        User user = User.builder()
//                .id(userPrincipal.getId())
//                .username(userPrincipal.getUsername())
//                .password(userPrincipal.getPassword())
//                .build();
//
//        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(50)).build();
//        user.setWallet(wallet);
//
//        FilterTransactionRequest filterRequest = FilterTransactionRequest.builder()
//                .transactionType("DEPOSIT")
//                .transactionStatus("SUCCEEDED")
//                .build();
//
//        Transaction tx1 = Transaction.builder().id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(100))
//                .type(TransactionType.DEPOSIT)
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//        Transaction tx2 = Transaction.builder().id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(150))
//                .type(TransactionType.DEPOSIT)
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//
//        List<Transaction> filteredTransactions = List.of(tx1, tx2);
//
//        when(userService.getUserById(userPrincipal.getId())).thenReturn(user);
//        when(transactionService.getFilteredTransactions(any(), any(FilterTransactionRequest.class)))
//                .thenReturn(filteredTransactions);
//
//        // Act & Assert
//        mockMvc.perform(get("/transactions/filter")
//                        .with(user(userPrincipal))
//                        .param("transactionType", "DEPOSIT")
//                        .param("transactionStatus", "SUCCEEDED"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("transaction/transactions"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("transactions"))
//                .andExpect(model().attributeExists("filterTransaction"))
//                .andExpect(model().attribute("user", user))
//                .andExpect(model().attribute("transactions", filteredTransactions))
//                .andExpect(model().attribute("filterTransaction",
//                        org.hamcrest.Matchers.hasProperty("transactionType", org.hamcrest.Matchers.is("DEPOSIT"))))
//                .andExpect(model().attribute("filterTransaction",
//                        org.hamcrest.Matchers.hasProperty("transactionStatus", org.hamcrest.Matchers.is("SUCCEEDED"))));
//    }




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

}
