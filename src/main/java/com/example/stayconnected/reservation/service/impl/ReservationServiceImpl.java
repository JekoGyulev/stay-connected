package com.example.stayconnected.reservation.service.impl;

import com.example.stayconnected.event.ReservationBookedEventPublisher;
import com.example.stayconnected.event.ReservationCancelledEventPublisher;
import com.example.stayconnected.event.payload.ReservationBookedEvent;
import com.example.stayconnected.event.payload.ReservationCancelledEvent;
import com.example.stayconnected.reservation.client.ReservationClient;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationClient reservationClient;
    private final WalletService walletService;
    private final UserService userService;
    private final ReservationBookedEventPublisher  reservationBookedEventPublisher;
    private final ReservationCancelledEventPublisher reservationCancelledEventPublisher;

    @Autowired
    public ReservationServiceImpl(ReservationClient reservationClient, WalletService walletService, UserService userService, ReservationBookedEventPublisher reservationBookedEventPublisher, ReservationCancelledEventPublisher reservationCancelledEventPublisher) {
        this.reservationClient = reservationClient;
        this.walletService = walletService;
        this.userService = userService;
        this.reservationBookedEventPublisher = reservationBookedEventPublisher;
        this.reservationCancelledEventPublisher = reservationCancelledEventPublisher;
    }


    @Override
    public BigDecimal getAveragePercentageOfReservationsByStatus(String status) {
        return this.reservationClient.getAveragePercentageOfReservationsByStatus(status).getBody();
    }

    @Override
    public long getTotalReservationsByStatus(String status) {
        ResponseEntity<Long> totalReservations = this.reservationClient.getTotalReservationsByStatus(status);
        return totalReservations.getBody() != null ? totalReservations.getBody() : 0;
    }

    @Override
    public PageResponse<ReservationResponse> getReservationsByUserId(UUID userId, int pageNumber, int pageSize) {

        PageResponse<ReservationResponse> reservationHistoryForUser =
                this.reservationClient.getReservationHistoryForUser(pageNumber, pageSize, userId);


        List<ReservationResponse> content = reservationHistoryForUser.getContent();

        if (content == null) {
            reservationHistoryForUser.setContent(Collections.emptyList());
        }

        return reservationHistoryForUser;
    }

    @Override
    public PageResponse<ReservationResponse> getReservationsByUserIdAndReservationStatus(UUID userId, String status, int pageNumber, int pageSize) {

        PageResponse<ReservationResponse> reservationHistoryForUserByReservationStatus =
                this.reservationClient.getReservationHistoryForUserByReservationStatus(pageNumber, pageSize, userId, status);


        List<ReservationResponse> content = reservationHistoryForUserByReservationStatus.getContent();

        if (content == null) {
            reservationHistoryForUserByReservationStatus.setContent(Collections.emptyList());
        }

        return reservationHistoryForUserByReservationStatus;
    }

    @Override
    public void cancel(UUID reservationId, UUID userId) {
        ResponseEntity<ReservationResponse> responseEntity = this.reservationClient.cancelReservation(reservationId);

        ReservationResponse response = responseEntity.getBody();

        this.walletService.reverseEarning(response.getTotalPrice(), response.getPropertyId());
        this.walletService.refund(userId, response.getTotalPrice());


        User user = this.userService.getUserById(userId);

        ReservationCancelledEvent event = ReservationCancelledEvent.builder()
                .userId(userId)
                .userEmail(user.getEmail())
                .username(user.getUsername())
                .reservationStartDate(response.getStartDate())
                .reservationEndDate(response.getEndDate())
                .reservationTotalPrice(response.getTotalPrice())
                .build();

        this.reservationCancelledEventPublisher.publish(event);
    }

    @Override
    public void create(CreateReservationRequest createReservationRequest, UUID ownerId) {
        this.reservationClient.createReservation(createReservationRequest);

        this.walletService.exchange(createReservationRequest, ownerId);

        ReservationBookedEvent event = ReservationBookedEvent.builder()
                .userId(createReservationRequest.getUserId())
                .userEmail(this.userService.getUserById(createReservationRequest.getUserId()).getEmail())
                .reservationStartDate(createReservationRequest.getStartDate())
                .reservationEndDate(createReservationRequest.getEndDate())
                .reservationTotalPrice(createReservationRequest.getTotalPrice())
                .build();

        this.reservationBookedEventPublisher.publish(event);
    }
}
