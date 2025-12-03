package com.example.stayconnected.reservation.service;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ReservationService {

    BigDecimal getAveragePercentageOfReservationsByStatus(String status);

    long getTotalReservationsByStatus(String status);

    List<ReservationResponse> getReservationsByUserId(UUID userId);

    void cancel(UUID reservationId, UUID userId);

    void create(CreateReservationRequest createReservationRequest, UUID ownerId);
}
