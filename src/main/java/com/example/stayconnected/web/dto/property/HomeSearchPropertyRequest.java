package com.example.stayconnected.web.dto.property;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeSearchPropertyRequest {

    private String country;
    private LocalDate checkIn;
    private LocalDate checkOut;


}
