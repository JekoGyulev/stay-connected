package com.example.stayconnected.email;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Override
    @EventListener
    @Async
    public void sendEmailWhenUserRegister(SuccessfulRegistrationEvent event) {
        log.info("Sending email for successfully registered user with email [%s] and username [%s]\n"
                        .formatted(event.getEmail(), event.getUsername()));

        // TODO: Implement sending real email
    }
}
