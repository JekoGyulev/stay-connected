package com.example.stayconnected.notification_preference.client;

import com.example.stayconnected.notification_preference.client.dto.NotificationPreferenceResponse;
import com.example.stayconnected.web.dto.email.UpsertNotificationPreferenceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient( name = "notification-preference-service", url = "http://localhost:8082/api/v1/notification-preferences")
public interface NotificationPreferenceClient {

    @GetMapping
    ResponseEntity<NotificationPreferenceResponse> getNotificationPreferenceByUserId(@RequestParam(value = "userId") UUID userId);

    @PostMapping
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreferenceRequest upsertNotificationPreferenceRequest);



}
