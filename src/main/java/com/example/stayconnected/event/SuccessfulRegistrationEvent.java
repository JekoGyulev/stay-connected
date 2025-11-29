package com.example.stayconnected.event;

import com.example.stayconnected.user.model.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SuccessfulRegistrationEvent {
    private User user;
    private String email;
}
