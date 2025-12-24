package com.example.stayconnected.email.service.impl;

import com.example.stayconnected.email.client.EmailClient;
import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.email.service.EmailService;
import com.example.stayconnected.event.InquiryHostEventPublisher;
import com.example.stayconnected.event.payload.HostInquiryEvent;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.email.ContactHostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailClient emailClient;
    private final InquiryHostEventPublisher  inquiryHostEventPublisher;

    @Autowired
    public EmailServiceImpl(EmailClient emailClient, InquiryHostEventPublisher inquiryHostEventPublisher) {
        this.emailClient = emailClient;
        this.inquiryHostEventPublisher = inquiryHostEventPublisher;
    }

    @Override
    public PageResponse<EmailResponse> getAllEmailsByUserId(int pageNumber, int pageSize, UUID userId) {
        return this.emailClient.getAllEmailsByUserId(pageNumber, pageSize, userId);
    }

    @Override
    public PageResponse<EmailResponse> getAllEmailsBySubjectContainingAndUserId(int pageNumber, int pageSize, String search, UUID userId) {
        return this.emailClient.getAllEmailsBySubjectContainingAndUserId(pageNumber, pageSize, search, userId);
    }

    @Override
    public PageResponse<EmailResponse> getAllEmailsByStatus(int pageNumber, int pageSize, UUID userId, String status) {
        return this.emailClient.getAllEmailsByUserIdAndStatusSorted(pageNumber, pageSize, userId, status);
    }


    @Override
    public long getTotalCountEmailsByUserIdAndStatus(UUID userId, String emailStatus) {
        return this.emailClient.getTotalCountEmailsByUserIdAndStatus(userId, emailStatus).getBody();
    }

    @Override
    public void publishInquiry(ContactHostRequest contactHostRequest, User user, Property property) {

        HostInquiryEvent event =  new HostInquiryEvent();
        event.setUserId(property.getOwner().getId());
        event.setUserEmail(user.getEmail());
        event.setHosterEmail(property.getOwner().getEmail());
        event.setSubject(contactHostRequest.getSubject());
        event.setBody(contactHostRequest.getBody());
        event.setInquiryType(contactHostRequest.getInquiryType());
        event.setPropertyTitle(property.getTitle());


        this.inquiryHostEventPublisher.publish(event);
    }
}
