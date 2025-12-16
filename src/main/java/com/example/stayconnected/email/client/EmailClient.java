package com.example.stayconnected.email.client;


import com.example.stayconnected.email.client.dto.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "email-service", url = "http://localhost:8082/api/v1/emails")
public interface EmailClient {

    @GetMapping
    List<EmailResponse> getAllEmailsByUserId(@RequestParam("userId") UUID userId);
}
