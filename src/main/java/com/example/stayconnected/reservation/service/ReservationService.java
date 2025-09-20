package com.example.stayconnected.reservation.service;

import com.example.stayconnected.reservation.model.Reservation;
import com.example.stayconnected.web.dto.ReservationInfoDTO;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    List<ReservationInfoDTO> getAllReservationsByUser(UUID id);

    // Create reservation
    // Cancel reservation
}
