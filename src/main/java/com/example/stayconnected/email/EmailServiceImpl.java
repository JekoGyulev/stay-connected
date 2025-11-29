package com.example.stayconnected.email;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.notification.enums.NotificationType;
import com.example.stayconnected.notification.model.Notification;
import com.example.stayconnected.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final String EMAIL_SUBJECT = "Welcome to our platform!";
    private static final String EMAIL_BODY =    """
                                                    Hi %s,
                                                        \s
                                                    Thanks for signing up! We're excited to have you on board.
                                                    Explore the platform at your own pace and let us know if you ever need help.
                                                        \s
                                                    Enjoy your stay!
                                                 """;


    private final MailSender mailSender;
    private final NotificationService notificationService;

    @Autowired
    public EmailServiceImpl(MailSender mailSender, NotificationService notificationService) {
        this.mailSender = mailSender;
        this.notificationService = notificationService;
    }

    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegistration(SuccessfulRegistrationEvent event) {

        Notification notification = Notification
                .builder()
                .user(event.getUser())
                .type(NotificationType.REGISTRATION)
                .createdOn(LocalDateTime.now())
                .subject(EMAIL_SUBJECT)
                .body(EMAIL_BODY.formatted(event.getUser().getUsername()))
                .build();


        this.notificationService.persist(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());
        message.setTo(event.getEmail());

        try {
            this.mailSender.send(message);
            log.info("Sending email for successfully registered user with email [{}] and username [{}]"
                   ,event.getEmail(), event.getUser().getUsername());
        } catch (Exception e) {
            log.error("Email failed due to : {}",e.getMessage());
        }
    }


}
