package com.example.stayconnected.notification_preference.client.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceResponse {

    private boolean notificationsEnabled;
    private boolean bookingConfirmationEnabled;
    private boolean bookingCancellationEnabled;
    private boolean passwordChangeEnabled;

}
