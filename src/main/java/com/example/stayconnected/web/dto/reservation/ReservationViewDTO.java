package com.example.stayconnected.web.dto.reservation;

import com.example.stayconnected.web.dto.property.PropertyViewDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReservationViewDTO {

    private UUID reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;
    private PropertyViewDTO propertyViewDTO;

}
