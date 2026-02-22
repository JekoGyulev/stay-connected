package com.example.stayconnected.reservation.client;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "reservation-svc", url = "http://localhost:8081/api/v1/reservations")
public interface ReservationClient {

    @GetMapping("/percentage")
    ResponseEntity<BigDecimal> getAveragePercentageOfReservationsByStatus(@RequestParam("status") String status);

    @GetMapping("/total")
    ResponseEntity<Long> getTotalReservationsByStatus(@RequestParam(value = "status") String status);

    @GetMapping("/unavailable-to-book")
    ResponseEntity<List<UUID>> getUnavailableToBookPropertyIds(@RequestParam(value = "checkIn") LocalDate startDate, @RequestParam(value = "checkOut") LocalDate endDate);

    @GetMapping
    PageResponse<ReservationResponse> getReservationHistoryForUser(
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam("userId") UUID userId);

    @GetMapping("/status")
    PageResponse<ReservationResponse> getReservationHistoryForUserByReservationStatus(
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "userId") UUID userId,
            @RequestParam(value = "reservationStatus") String reservationStatus
    );

    @PutMapping("/cancellation")
    ResponseEntity<ReservationResponse> cancelReservation(@RequestParam("reservationId") UUID id);

    @PostMapping
    ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationRequest createReservationRequest);
}
