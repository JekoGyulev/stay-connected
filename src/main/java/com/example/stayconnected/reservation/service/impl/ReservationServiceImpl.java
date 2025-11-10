package com.example.stayconnected.reservation.service.impl;

import com.example.stayconnected.reservation.enums.ReservationStatus;
import com.example.stayconnected.reservation.model.Reservation;
import com.example.stayconnected.reservation.repository.ReservationRepository;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.web.dto.reservation.ReservationInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<ReservationInfoRequest> getAllReservationsByUser(UUID id) {
        List<Reservation> reservations = this.reservationRepository.findAllByUserIdOrderByCreatedAtDesc(id);


        List<ReservationInfoRequest> reservationRequests = reservations.stream()
                .map(ReservationInfoRequest::new)
                .toList();

        return reservationRequests;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return this.reservationRepository.findAll();
    }

    @Override
    public long getTotalCompletedReservations() {
        return this.reservationRepository.countAllByStatus(ReservationStatus.PAID);
    }

    @Override
    public BigDecimal getPercentageCompletedReservations() {

        long totalCompletedReservations = getTotalCompletedReservations();
        long totalReservations = getAllReservations().size();

        if (totalReservations == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf((totalCompletedReservations * 100.00) / totalReservations);
    }

    // Have at least 1 logging message -> log.info("Successfully done {your operation}")
}
