package com.example.stayconnected.reservation.service;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ReservationService {

    BigDecimal getAveragePercentageOfReservationsByStatus(String status);

    long getTotalReservationsByStatus(String status);

    PageResponse<ReservationResponse> getReservationsByUserId(UUID userId, int pageNumber, int pageSize);

    PageResponse<ReservationResponse> getReservationsByUserIdAndReservationStatus(UUID userId, String status, int pageNumber, int pageSize);

    void cancel(UUID reservationId, UUID userId);

    void create(CreateReservationRequest createReservationRequest, UUID ownerId);
}
