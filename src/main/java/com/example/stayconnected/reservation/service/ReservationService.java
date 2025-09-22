package com.example.stayconnected.reservation.service;

import com.example.stayconnected.web.dto.reservation.ReservationInfoRequest;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    List<ReservationInfoRequest> getAllReservationsByUser(UUID id);

    // Create reservation
    // Cancel reservation
}
