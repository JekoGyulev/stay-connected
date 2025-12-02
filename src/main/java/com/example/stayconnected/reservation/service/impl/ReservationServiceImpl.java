package com.example.stayconnected.reservation.service.impl;

import com.example.stayconnected.reservation.client.ReservationClient;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationClient reservationClient;
    private final WalletService walletService;

    @Autowired
    public ReservationServiceImpl(ReservationClient reservationClient, WalletService walletService) {
        this.reservationClient = reservationClient;
        this.walletService = walletService;
    }


    @Override
    public long getTotalReservations() {
        ResponseEntity<Long> totalReservations = this.reservationClient.getTotalReservations();
        return totalReservations.getBody() != null ? totalReservations.getBody() : 0;
    }

    @Override
    public List<ReservationResponse> getReservationsByUserId(UUID userId) {
        ResponseEntity<List<ReservationResponse>> reservationHistoryForUser = this.reservationClient.getReservationHistoryForUser(userId);
        return reservationHistoryForUser.getBody() != null ? reservationHistoryForUser.getBody() : Collections.emptyList();
    }

    @Override
    public void cancel(UUID id) {
        this.reservationClient.cancelReservation(id);
    }

    @Override
    public void create(CreateReservationRequest createReservationRequest, UUID ownerId) {
        this.reservationClient.createReservation(createReservationRequest);

        this.walletService.exchange(createReservationRequest, ownerId);
    }
}
