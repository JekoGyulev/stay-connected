package com.example.stayconnected.email.service;

import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.email.ContactHostRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface EmailService {

    PageResponse<EmailResponse> getAllEmailsByUserId(int pageNumber, int pageSize, UUID userId);

    PageResponse<EmailResponse> getAllEmailsBySubjectContainingAndUserId(int pageNumber, int pageSize, String search, UUID userId);

    PageResponse<EmailResponse> getAllEmailsByStatus(int pageNumber, int pageSize, UUID userId, String status);

    long getTotalCountEmailsByUserIdAndStatus(UUID userId, String emailStatus);

    void publishInquiry(ContactHostRequest contactHostRequest, User user, Property property);
}
