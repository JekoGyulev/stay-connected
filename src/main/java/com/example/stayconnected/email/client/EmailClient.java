package com.example.stayconnected.email.client;


import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "email-service", url = "http://localhost:8082/api/v1/emails")
public interface EmailClient {


    @GetMapping
    PageResponse<EmailResponse> getAllEmailsBySubjectContainingAndUserId(
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "search") String search,
            @RequestParam(value = "userId") UUID userId
    );

    @GetMapping
    PageResponse<EmailResponse> getAllEmailsByUserId(
                                                @RequestParam(value = "pageNumber") int pageNumber,
                                                @RequestParam(value = "pageSize") int pageSize,
                                                @RequestParam("userId") UUID userId
    );

    @GetMapping("/status")
    PageResponse<EmailResponse> getAllEmailsByUserIdAndStatusSorted(
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "userId") UUID userId,
            @RequestParam(value = "status") String emailStatus
    );



    @GetMapping("/total")
    ResponseEntity<Long> getTotalCountEmailsByUserIdAndStatus(@RequestParam(value = "userId") UUID userId,
                                                              @RequestParam(value = "status") String emailStatus);
}
