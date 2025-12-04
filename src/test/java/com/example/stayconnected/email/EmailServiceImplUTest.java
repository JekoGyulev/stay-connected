package com.example.stayconnected.email;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.notification.enums.NotificationType;
import com.example.stayconnected.notification.model.Notification;
import com.example.stayconnected.notification.service.NotificationService;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EmailServiceImplUTest {

    @Mock
    private  MailSender mailSender;
    @Mock
    private  NotificationService notificationService;

    @InjectMocks
    private EmailServiceImpl emailServiceImpl;


    @Test
    void testHandleRegistration_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");

        var event = new SuccessfulRegistrationEvent(user, "test@example.com");

        // Act
        emailServiceImpl.handleRegistration(event);

        // Assert: notificationService.persist() is called with correct Notification
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1)).persist(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertEquals(user, notification.getUser());
        assertEquals(NotificationType.REGISTRATION, notification.getType());
        assertEquals("Welcome to our platform!", notification.getSubject());
        assertTrue(notification.getBody().contains("testUser"));

        // Assert: mailSender.send() is called with correct SimpleMailMessage
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());
        SimpleMailMessage sentMessage = mailCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals(notification.getSubject(), sentMessage.getSubject());
        assertEquals(notification.getBody(), sentMessage.getText());
    }

    @Test
    void testHandleRegistration_EmailFails() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");

        var event = new SuccessfulRegistrationEvent(user, "test@example.com");

        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailServiceImpl.handleRegistration(event);

        // Assert: notificationService.persist() is still called
        verify(notificationService, times(1)).persist(any(Notification.class));

        // Assert: mailSender.send() attempted and exception handled
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
