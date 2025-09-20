package com.example.stayconnected.web.dto;

import com.example.stayconnected.reservation.model.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationInfoDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private String reservationStatus;
    private LocalDateTime createdAt;

    public ReservationInfoDTO() {}

    public ReservationInfoDTO(Reservation reservation) {
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.price = reservation.getTotalPrice();
        this.reservationStatus = reservation.getStatus().name();
        this.createdAt = reservation.getCreatedAt();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
