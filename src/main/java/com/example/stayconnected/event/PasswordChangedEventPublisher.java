package com.example.stayconnected.event;


import com.example.stayconnected.event.payload.PasswordChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.stayconnected.config.KafkaConfiguration.PASSWORD_CHANGED_EVENT_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class PasswordChangedEventPublisher {


    private final KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate;

    @Autowired
    public PasswordChangedEventPublisher(KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PasswordChangedEvent passwordChangedEvent) {
        kafkaTemplate.send(PASSWORD_CHANGED_EVENT_KAFKA_TOPIC_NAME, passwordChangedEvent);

        log.info("Successfully sent event to topic [%s] for user [%s]"
                .formatted(PASSWORD_CHANGED_EVENT_KAFKA_TOPIC_NAME, passwordChangedEvent.getUsername()));
    }


}
