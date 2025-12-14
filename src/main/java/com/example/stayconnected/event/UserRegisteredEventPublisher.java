package com.example.stayconnected.event;


import com.example.stayconnected.event.payload.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.stayconnected.config.KafkaConfiguration.USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class UserRegisteredEventPublisher {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Autowired
    public UserRegisteredEventPublisher(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(UserRegisteredEvent event) {
        kafkaTemplate.send("user-registered-event.v1",  event);

        log.info("Successfully sent event to topic = [%s] for user with id = [%s]"
                .formatted(USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME, event.getUserId()));
    }
}
