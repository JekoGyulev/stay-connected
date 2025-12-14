package com.example.stayconnected.event.payload;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRegisteredEvent {
    private UUID userId;
    private String username;
    private String email;
}
