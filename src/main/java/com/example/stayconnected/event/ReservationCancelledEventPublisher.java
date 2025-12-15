package com.example.stayconnected.event;

import com.example.stayconnected.event.payload.ReservationCancelledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.stayconnected.config.KafkaConfiguration.RESERVATION_CANCELLED_EVENT_KAFKA_TOPIC_NAME;
import static com.example.stayconnected.config.KafkaConfiguration.USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class ReservationCancelledEventPublisher {

    private final KafkaTemplate<String, ReservationCancelledEvent> kafkaTemplate;

    @Autowired
    public ReservationCancelledEventPublisher(KafkaTemplate<String, ReservationCancelledEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ReservationCancelledEvent reservationCancelledEvent) {
        kafkaTemplate.send(RESERVATION_CANCELLED_EVENT_KAFKA_TOPIC_NAME,  reservationCancelledEvent);

        log.info("Successfully sent event to topic = [%s] for user with id = [%s]"
                .formatted(USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME, reservationCancelledEvent.getUserId()));
    }




}
