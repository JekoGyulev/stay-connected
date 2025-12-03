package com.example.stayconnected.utils;

import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ReservationUtils {

    public static List<ReservationResponse> getBookedReservationsOnly(List<ReservationResponse> reservations) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus().equals("BOOKED"))
                .toList();
    }

    public static List<ReservationResponse> getCancelledReservationsOnly(List<ReservationResponse> reservations) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus().equals("CANCELLED"))
                .toList();
    }

}
