package com.example.stayconnected.web.dto;

import com.example.stayconnected.property.model.Property;

import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.property.PropertyViewDTO;
import com.example.stayconnected.web.dto.reservation.ReservationViewDTO;
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

    public static PropertyViewDTO viewFromProperty(Property property) {
        return PropertyViewDTO.builder()
                .title(property.getTitle())
                .photoUrl(property.getImages().get(0).getImageURL())
                .country(property.getLocation().getCountry())
                .city(property.getLocation().getCity())
                .build();
    }

    public static ReservationViewDTO fromPropertyViewAndResponse(PropertyViewDTO propertyViewDTO, ReservationResponse reservationResponse) {
        return ReservationViewDTO.builder()
                .reservationId(reservationResponse.getReservationId())
                .startDate(reservationResponse.getStartDate())
                .endDate(reservationResponse.getEndDate())
                .status(reservationResponse.getStatus())
                .totalPrice(reservationResponse.getTotalPrice())
                .propertyViewDTO(propertyViewDTO)
                .build();
    }


}
