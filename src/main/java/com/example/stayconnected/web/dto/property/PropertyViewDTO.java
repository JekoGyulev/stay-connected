package com.example.stayconnected.web.dto.property;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class PropertyViewDTO {
    private String title;
    private String photoUrl;
    private String country;
    private String city;
}
