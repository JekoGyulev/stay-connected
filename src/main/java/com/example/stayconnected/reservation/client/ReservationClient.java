package com.example.stayconnected.reservation.client;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "reservation-svc", url = "http://localhost:8081/api/v1/reservations")
public interface ReservationClient {

    @GetMapping("/total")
    ResponseEntity<Long> getTotalReservations();

    @GetMapping
    ResponseEntity<List<ReservationResponse>> getReservationHistoryForUser(@RequestParam("userId") UUID userId);

    @PutMapping("/cancellation")
    ResponseEntity<ReservationResponse> cancelReservation(@RequestParam("reservationId") UUID id);

    @PostMapping
    ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationRequest createReservationRequest);
}
