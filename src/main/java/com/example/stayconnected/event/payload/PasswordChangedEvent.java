package com.example.stayconnected.event.payload;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangedEvent {

    private UUID userId;
    private String username;
    private String userEmail;
    private LocalDateTime changedAt;

}
