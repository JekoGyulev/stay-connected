package com.example.stayconnected.web.dto;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.user.ProfileEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static ProfileEditRequest fromUser(User user) {
        return new ProfileEditRequest(
                                        user.getFirstName(),
                                        user.getLastName(),
                                        user.getEmail(),
                                        user.getUsername()
        );
    }

    public static EditPropertyRequest fromProperty(Property property) {

        LocationRequest locationRequest = new LocationRequest(property.getLocation().getCountry(), property.getLocation().getCity(), property.getLocation().getAddress());

        EditPropertyRequest editPropertyRequest = EditPropertyRequest.builder()
                .title(property.getTitle())
                .description(property.getDescription())
                .category(property.getCategoryType())
                .pricePerNight(property.getPricePerNight())
                .location(locationRequest)
                .amenities(property.getAmenities())
                .build();

        return editPropertyRequest;
    }
}
