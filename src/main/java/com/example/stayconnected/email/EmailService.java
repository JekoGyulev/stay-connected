package com.example.stayconnected.email;

import com.example.stayconnected.event.SuccessfulRegistrationEvent;

public interface EmailService {
    void handleRegistration(SuccessfulRegistrationEvent event);
}
