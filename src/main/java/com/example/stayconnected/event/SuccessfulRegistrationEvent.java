package com.example.stayconnected.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SuccessfulRegistrationEvent {
    private String email;
    private String username;
}
