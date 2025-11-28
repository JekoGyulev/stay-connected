package com.example.stayconnected.notification.service.impl;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.notification.model.Notification;
import com.example.stayconnected.notification.repository.NotificationRepository;
import com.example.stayconnected.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public void persist(Notification notification) {
        this.notificationRepository.save(notification);
        log.info("Notification created for user with username {}",notification.getUser().getUsername());
    }
}
