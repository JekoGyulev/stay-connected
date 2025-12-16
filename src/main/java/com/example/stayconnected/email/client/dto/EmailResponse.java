package com.example.stayconnected.email.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmailResponse {

    private UUID emailId;
    private String subject;
    private String emailTrigger;
    private String emailStatus;
    private LocalDateTime createdAt;
    private UUID userId;

}
