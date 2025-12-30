package com.example.stayconnected.notification_preference.service.impl;

import com.example.stayconnected.notification_preference.client.NotificationPreferenceClient;
import com.example.stayconnected.notification_preference.client.dto.NotificationPreferenceResponse;
import com.example.stayconnected.notification_preference.service.NotificationPreferenceService;
import com.example.stayconnected.web.dto.email.UpsertNotificationPreferenceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceClient notificationPreferenceClient;


    @Autowired
    public NotificationPreferenceServiceImpl(NotificationPreferenceClient notificationPreferenceClient) {
        this.notificationPreferenceClient = notificationPreferenceClient;
    }


    @Override
    public NotificationPreferenceResponse getNotificationPreferenceByUserId(UUID userId) {

        NotificationPreferenceResponse response = this.notificationPreferenceClient
                .getNotificationPreferenceByUserId(userId).getBody();

        return response;
    }

    @Override
    public void upsertNotificationPreference(UpsertNotificationPreferenceRequest request) {
        this.notificationPreferenceClient.upsertNotificationPreference(request);
    }
}
