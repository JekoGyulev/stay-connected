package com.example.stayconnected.reservation.service;

import com.example.stayconnected.reservation.model.Reservation;
import com.example.stayconnected.web.dto.reservation.ReservationInfoRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReservationService {

    List<ReservationInfoRequest> getAllReservationsByUser(UUID id);

    List<Reservation> getAllReservations();

    long getTotalCompletedReservations();

    // Create reservation
    // Cancel reservation
}
