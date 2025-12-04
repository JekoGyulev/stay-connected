package com.example.stayconnected.reservation;


import com.example.stayconnected.reservation.client.ReservationClient;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.impl.ReservationServiceImpl;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplUTest {

    @Mock
    private  ReservationClient reservationClient;
    @Mock
    private  WalletService walletService;
    @Mock
    private  UserService userService;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;


    @Test
    void testGetTotalReservationsByStatus_ReturnsValue() {

        String status = "BOOKED";

        Long expected = 2L;

        when(reservationClient.getTotalReservationsByStatus(status))
                .thenReturn(ResponseEntity.ok(expected));


        long result = reservationServiceImpl.getTotalReservationsByStatus(status);
        assertEquals(expected, result);
        verify(reservationClient, times(1)).getTotalReservationsByStatus(status);
    }

    @Test
    void testGetTotalReservationsByStatus_ReturnEmptyList_thenReturnEmptyList() {

        String status = "BOOKED";

        Long expected = 0L;

        when(reservationClient.getTotalReservationsByStatus(status))
                .thenReturn(ResponseEntity.ok(expected));

        assertEquals(0 ,  reservationServiceImpl.getTotalReservationsByStatus(status));
    }



    @Test
    void testGetReservationsByUserId_ReturnsValue() {

        UUID userId = UUID.randomUUID();

        ReservationResponse response1 = ReservationResponse.builder().build();
        ReservationResponse response2 = ReservationResponse.builder().build();

        List<ReservationResponse> expected = Arrays.asList(response1, response2);

        when(reservationClient.getReservationHistoryForUser(userId))
                .thenReturn(ResponseEntity.ok(expected));

        List<ReservationResponse> actual = reservationServiceImpl.getReservationsByUserId(userId);

        assertEquals(expected, actual);
        verify(reservationClient, times(1)).getReservationHistoryForUser(userId);
    }

    @Test
    void testGetReservationsByUserId_andDoNotExist_thenReturnEmptyList() {

        UUID userId = UUID.randomUUID();



        when(reservationClient.getReservationHistoryForUser(userId))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<ReservationResponse> actual = reservationServiceImpl.getReservationsByUserId(userId);

        assertEquals(0, actual.size());
        verify(reservationClient, times(1)).getReservationHistoryForUser(userId);
    }


    @Test
    void whenThereIsRequestAndUserId_thenCreateReservationAndSaveToDb() {

        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        Wallet wallet = Wallet.builder()
                .owner(user)
                .balance(BigDecimal.valueOf(20))
                .build();

        CreateReservationRequest createReservationRequest = CreateReservationRequest
                .builder()
                .userId(userId)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .totalPrice(BigDecimal.valueOf(200))
                .build();

        reservationServiceImpl.create(createReservationRequest, wallet.getOwner().getId());

        verify(reservationClient, times(1)).createReservation(createReservationRequest);
        verify(walletService, times(1)).exchange(createReservationRequest, wallet.getOwner().getId());
    }


    @Test
    void whenThereIsReservationId_andUserId_thenCancelReservation() {

        UUID userId = UUID.randomUUID();

        UUID reservationId = UUID.randomUUID();

        UUID propertyId = UUID.randomUUID();

        ReservationResponse response = ReservationResponse
                .builder()
                .status("BOOKED")
                .totalPrice(BigDecimal.valueOf(200))
                .propertyId(propertyId)
                .reservationId(reservationId)
                .build();

        when(reservationClient.cancelReservation(reservationId))
                .thenReturn(ResponseEntity.ok(response));


        reservationServiceImpl.cancel(reservationId, userId);

        verify(reservationClient, times(1)).cancelReservation(reservationId);
        verify(walletService, times(1)).refund(userId, response.getTotalPrice());
        verify(walletService, times(1)).reverseEarning(response.getTotalPrice(), response.getPropertyId());
    }




}
