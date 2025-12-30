package com.example.stayconnected.web.dto.email;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpsertNotificationPreferenceRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private boolean notificationsEnabled;
    @NotNull
    private boolean bookingConfirmationEnabled;
    @NotNull
    private boolean bookingCancellationEnabled;
    @NotNull
    private boolean passwordChangeEnabled;

}
