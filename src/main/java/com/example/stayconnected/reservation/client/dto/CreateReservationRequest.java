package com.example.stayconnected.reservation.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateReservationRequest {
    private UUID userId;
    private UUID propertyId;
    @NotNull(message = "Select check-in")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @NotNull(message = "Select check-out")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    @NotNull
    @Positive
    private BigDecimal totalPrice;
}
