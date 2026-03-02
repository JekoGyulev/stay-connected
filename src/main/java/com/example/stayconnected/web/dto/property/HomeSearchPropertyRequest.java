package com.example.stayconnected.web.dto.property;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeSearchPropertyRequest {

    private String country;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkIn;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate checkOut;


}
