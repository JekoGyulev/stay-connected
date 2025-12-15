package com.example.stayconnected.event.payload;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReservationBookedEvent {

    private UUID userId;
    private String userEmail;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private BigDecimal reservationTotalPrice;

}
