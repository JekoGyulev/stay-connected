package com.example.stayconnected.notification_preference.service;

import com.example.stayconnected.notification_preference.client.dto.NotificationPreferenceResponse;
import com.example.stayconnected.web.dto.email.UpsertNotificationPreferenceRequest;

import java.util.UUID;

public interface NotificationPreferenceService {

    NotificationPreferenceResponse getNotificationPreferenceByUserId(UUID userId);

    void upsertNotificationPreference(UpsertNotificationPreferenceRequest request);

}
