package com.example.stayconnected.reservation.service.impl;

import com.example.stayconnected.reservation.client.ReservationClient;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationClient reservationClient;

    @Autowired
    public ReservationServiceImpl(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
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
}
