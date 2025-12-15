package com.example.stayconnected.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfiguration {

    public static final String USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME = "user-registered-event.v1";
    public static final String RESERVATION_BOOKED_EVENT_KAFKA_TOPIC_NAME = "reservation-booked-event.v1";
    public static final String RESERVATION_CANCELLED_EVENT_KAFKA_TOPIC_NAME = "reservation-cancelled-event.v1";

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME).build();
    }

    @Bean
    public NewTopic reservationBookedTopic() {
        return TopicBuilder.name(RESERVATION_BOOKED_EVENT_KAFKA_TOPIC_NAME).build();
    }

    @Bean
    public NewTopic reservationCancelledTopic() {
        return TopicBuilder.name(RESERVATION_CANCELLED_EVENT_KAFKA_TOPIC_NAME).build();
    }
}
