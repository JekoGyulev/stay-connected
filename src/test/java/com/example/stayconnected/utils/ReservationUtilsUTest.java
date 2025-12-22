//package com.example.stayconnected.utils;
//
//import com.example.stayconnected.reservation.client.dto.ReservationResponse;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//public class ReservationUtilsUTest {
//
//
//    @Test
//    void whenThereAreReservations_thenGetTheBookedOnesOnly() {
//
//        ReservationResponse reservationResponse1 = ReservationResponse
//                .builder()
//                .status("BOOKED")
//                .build();
//
//        ReservationResponse reservationResponse2 = ReservationResponse
//                .builder()
//                .status("BOOKED")
//                .build();
//
//
//        ReservationResponse reservationResponse3 = ReservationResponse
//                .builder()
//                .status("CANCELLED")
//                .build();
//
//
//        List<ReservationResponse> reservationResponses = List.of(reservationResponse1, reservationResponse2, reservationResponse3);
//
//        List<ReservationResponse> bookedReservationsOnly = ReservationUtils.getBookedReservationsOnly(reservationResponses);
//
//        assertEquals(List.of(reservationResponse1, reservationResponse2), bookedReservationsOnly);
//    }
//
//
//    @Test
//    void whenThereAreReservations_thenGetCancelledOnesOnly() {
//
//        ReservationResponse reservationResponse1 = ReservationResponse
//                .builder()
//                .status("BOOKED")
//                .build();
//
//        ReservationResponse reservationResponse2 = ReservationResponse
//                .builder()
//                .status("BOOKED")
//                .build();
//
//
//        ReservationResponse reservationResponse3 = ReservationResponse
//                .builder()
//                .status("CANCELLED")
//                .build();
//
//
//        List<ReservationResponse> reservationResponses = List.of(reservationResponse1, reservationResponse2, reservationResponse3);
//
//        List<ReservationResponse> cancelledReservationsOnly = ReservationUtils.getCancelledReservationsOnly(reservationResponses);
//
//        assertEquals(List.of(reservationResponse3), cancelledReservationsOnly);
//    }
//
//
//
//}
