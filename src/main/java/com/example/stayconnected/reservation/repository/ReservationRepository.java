package com.example.stayconnected.reservation.repository;

import com.example.stayconnected.reservation.enums.ReservationStatus;
import com.example.stayconnected.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findAllByUserIdOrderByCreatedAtDesc(UUID user_id);

    long countAllByStatus(ReservationStatus reservationStatus);

    long countAllByStatusAndCreatedAtBetween(ReservationStatus status,
                                             LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
