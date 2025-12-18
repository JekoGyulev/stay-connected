package com.example.stayconnected.email.service;

import com.example.stayconnected.email.client.dto.EmailResponse;

import java.util.List;
import java.util.UUID;

public interface EmailService {

    List<EmailResponse> getAllEmailsByUserId(UUID userId);

    List<EmailResponse> getAllEmailsBySubjectContainingAndUserId(String search, UUID userId);

    List<EmailResponse> getAllSentEmails(List<EmailResponse> emailResponses);

    List<EmailResponse> getAllFailedEmails(List<EmailResponse> emailResponses);
}
