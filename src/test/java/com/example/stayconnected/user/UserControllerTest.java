package com.example.stayconnected.user;

import com.example.stayconnected.dashboard.DashboardStatsService;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
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

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_then() {}
}