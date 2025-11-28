package com.example.stayconnected.notification.service;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.notification.model.Notification;

public interface NotificationService {
    void persist(Notification notification);
}
