package com.example.stayconnected.reservation.service;

import com.example.stayconnected.reservation.client.dto.ReservationResponse;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    List<ReservationResponse> getReservationsByUserId(UUID userId);
}
