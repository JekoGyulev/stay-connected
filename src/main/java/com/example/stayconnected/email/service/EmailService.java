package com.example.stayconnected.email.service;

import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.reservation.client.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface EmailService {

    PageResponse<EmailResponse> getAllEmailsByUserId(int pageNumber, int pageSize, UUID userId);

    PageResponse<EmailResponse> getAllEmailsBySubjectContainingAndUserId(int pageNumber, int pageSize, String search, UUID userId);

    PageResponse<EmailResponse> getAllEmailsByStatus(int pageNumber, int pageSize, UUID userId, String status);

    long getTotalCountEmailsByUserIdAndStatus(UUID userId, String emailStatus);
}
