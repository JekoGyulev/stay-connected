package com.example.stayconnected.web.dto;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.location.LocationRequest;
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

//    public static CreatePropertyRequest fromProperty(Property property) {
//
//        LocationRequest locationRequest = new LocationRequest(property.getLocation().getCountry(), property.getLocation().getCity(), property.getLocation().getAddress());
//
//
//    }
}
