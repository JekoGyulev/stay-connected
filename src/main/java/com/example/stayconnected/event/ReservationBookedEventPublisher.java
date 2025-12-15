package com.example.stayconnected.event;


import com.example.stayconnected.event.payload.ReservationBookedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.stayconnected.config.KafkaConfiguration.RESERVATION_BOOKED_EVENT_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class ReservationBookedEventPublisher {

    private final KafkaTemplate<String, ReservationBookedEvent> kafkaTemplate;

    @Autowired
    public ReservationBookedEventPublisher(KafkaTemplate<String, ReservationBookedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(ReservationBookedEvent reservationBookedEvent) {
        kafkaTemplate.send(RESERVATION_BOOKED_EVENT_KAFKA_TOPIC_NAME, reservationBookedEvent);

        log.info("Successfully sent event to topic [%s] for user with id [%s]"
                .formatted(RESERVATION_BOOKED_EVENT_KAFKA_TOPIC_NAME, reservationBookedEvent.getUserId()));
    }



}
