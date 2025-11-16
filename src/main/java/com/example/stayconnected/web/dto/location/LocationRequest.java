package com.example.stayconnected.web.dto.location;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    @NotBlank(message = "Please enter country")
    private String country;
    @NotBlank(message = "Please enter city")
    private String city;
    @NotBlank(message = "Please enter address")
    private String address;
}
