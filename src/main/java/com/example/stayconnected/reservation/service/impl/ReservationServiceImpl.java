package com.example.stayconnected.reservation.service.impl;

import com.example.stayconnected.reservation.model.Reservation;
import com.example.stayconnected.reservation.repository.ReservationRepository;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.web.dto.ReservationInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<ReservationInfoDTO> getAllReservationsByUser(UUID id) {
        List<Reservation> reservations = this.reservationRepository.findAllByUserIdOrderByCreatedAtDesc(id);


        List<ReservationInfoDTO> reservationInfoDTOS = reservations.stream()
                .map(ReservationInfoDTO::new)
                .toList();

        return reservationInfoDTOS;
    }

    // Have at least 1 logging message -> log.info("Successfully done {your operation}")
}
