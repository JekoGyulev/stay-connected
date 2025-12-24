package com.example.stayconnected.event;

import com.example.stayconnected.event.payload.HostInquiryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.stayconnected.config.KafkaConfiguration.INQUIRY_HOST_EVENT_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class InquiryHostEventPublisher {

    private final KafkaTemplate<String, HostInquiryEvent> kafkaTemplate;

    @Autowired
    public InquiryHostEventPublisher(KafkaTemplate<String, HostInquiryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(HostInquiryEvent inquiryEvent) {
        kafkaTemplate.send(INQUIRY_HOST_EVENT_KAFKA_TOPIC_NAME, inquiryEvent);

        log.info("Successfully sent event to topic [%s] for user [%s]"
                .formatted(INQUIRY_HOST_EVENT_KAFKA_TOPIC_NAME, inquiryEvent.getUserEmail()));
    }


}
