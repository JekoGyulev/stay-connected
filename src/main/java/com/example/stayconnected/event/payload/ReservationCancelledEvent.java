package com.example.stayconnected.event.payload;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReservationCancelledEvent {

    private UUID userId;
    private String userEmail;
    private String username;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private BigDecimal reservationTotalPrice;

}
