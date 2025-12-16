package com.example.stayconnected.web.dto.email;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmailViewDTO {

    private UUID id;
    private String subject;
    private String trigger;
    private String status;
    private LocalDateTime createdAt;

}
