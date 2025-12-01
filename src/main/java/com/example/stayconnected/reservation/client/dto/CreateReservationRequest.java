package com.example.stayconnected.reservation.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateReservationRequest {
    private UUID userId;
    private UUID propertyId;
    @NotNull(message = "Select check-in")
    private LocalDate startDate;
    @NotNull(message = "Select check-out")
    private LocalDate endDate;
    @NotNull
    @Positive
    private BigDecimal totalPrice;
}
