package com.example.stayconnected.email.service.impl;

import com.example.stayconnected.email.client.EmailClient;
import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailClient emailClient;

    @Autowired
    public EmailServiceImpl(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    @Override
    public List<EmailResponse> getAllEmailsByUserId(UUID userId) {
        return this.emailClient.getAllEmailsByUserId(userId);
    }

    @Override
    public List<EmailResponse> getAllSentEmails(List<EmailResponse> emailResponses) {
        return emailResponses
                .stream()
                .filter(response -> response.getEmailStatus().equals("SENT"))
                .toList();
    }

    @Override
    public List<EmailResponse> getAllFailedEmails(List<EmailResponse> emailResponses) {
        return emailResponses
                .stream()
                .filter(response -> response.getEmailStatus().equals("FAILED"))
                .toList();
    }
}
