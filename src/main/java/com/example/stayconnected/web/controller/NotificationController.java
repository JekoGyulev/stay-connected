package com.example.stayconnected.web.controller;


import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.email.service.EmailService;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.email.EmailViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailService emailService;
    private final UserService userService;


    @Autowired
    public NotificationController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView showEmailsPage(
                                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        PageResponse<EmailResponse> emailResponses = this.emailService.getAllEmailsByUserId(pageNumber, pageSize, user.getId());

        long countSentEmails = this.emailService.getTotalCountEmailsByUserIdAndStatus(user.getId(), "SENT");
        long countFailedEmails = this.emailService.getTotalCountEmailsByUserIdAndStatus(user.getId(), "FAILED");
        long countTotalEmails = countFailedEmails + countSentEmails;

        int totalPages = emailResponses.getTotalPages();
        long totalElements = emailResponses.getTotalElements();

        String baseUrl = "/notifications";
        String queryParameters="";


        List<EmailViewDTO> emails = emailResponses.getContent().stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("emails", emails);
        modelAndView.addObject("countSentEmails", countSentEmails);
        modelAndView.addObject("countFailedEmails", countFailedEmails);
        modelAndView.addObject("countTotalEmails", countTotalEmails);
        modelAndView.addObject("user", user);
        modelAndView.addObject("filter", "ALL");

        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("queryParameters", queryParameters);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView showEmailsPageBySearch(
                                                @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
                                                @RequestParam(value = "value") String search,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        UUID userId = user.getId();


        PageResponse<EmailResponse> allEmailsBySubjectContaining = this.emailService
                .getAllEmailsBySubjectContainingAndUserId(pageNumber, pageSize, search, userId);


        long countSentEmails = this.emailService.getTotalCountEmailsByUserIdAndStatus(userId, "SENT");
        long countFailedEmails = this.emailService.getTotalCountEmailsByUserIdAndStatus(userId, "FAILED");
        long countTotalEmails = countFailedEmails + countSentEmails;

        List<EmailViewDTO> viewDTOS = allEmailsBySubjectContaining.getContent().stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();

        int totalPages = allEmailsBySubjectContaining.getTotalPages();
        long totalElements = allEmailsBySubjectContaining.getTotalElements();

        String baseUrl = "/notifications/search";
        String queryParameters="&value=%s".formatted(search);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("emails", viewDTOS);
        modelAndView.addObject("countSentEmails", countSentEmails);
        modelAndView.addObject("countFailedEmails", countFailedEmails);
        modelAndView.addObject("countTotalEmails", countTotalEmails);
        modelAndView.addObject("user", user);
        modelAndView.addObject("filter", "ALL");

        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("queryParameters", queryParameters);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);


        return modelAndView;
    }

    @GetMapping("/sent")
    public ModelAndView showSentEmailsPage(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        PageResponse<EmailResponse> pageResponse = this.emailService.getAllEmailsByStatus(pageNumber, pageSize, user.getId(), "SENT");

        List<EmailViewDTO> sentEmails = pageResponse.getContent()
                .stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();


        long countFailedEmails = this.emailService.getTotalCountEmailsByUserIdAndStatus(user.getId(), "FAILED");
        long countTotalEmails = pageResponse.getTotalElements() + countFailedEmails;

        int totalPages = pageResponse.getTotalPages();
        long totalElements = pageResponse.getTotalElements();

        String baseUrl = "/notifications/sent";
        String queryParameters = "";


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("user", user);
        modelAndView.addObject("emails", sentEmails);
        modelAndView.addObject("countTotalEmails", countTotalEmails);
        modelAndView.addObject("countSentEmails", pageResponse.getTotalElements());
        modelAndView.addObject("countFailedEmails", countFailedEmails);
        modelAndView.addObject("filter", "SENT");

        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("queryParameters", queryParameters);

        return modelAndView;
    }

    @GetMapping("/failed")
    public ModelAndView showFailedEmailsPage(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        PageResponse<EmailResponse> pageResponse = this.emailService.getAllEmailsByStatus(pageNumber, pageSize, user.getId(), "FAILED");

        List<EmailViewDTO> failedEmails = pageResponse.getContent()
                .stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();

        long countTotalSentEmails =  this.emailService.getTotalCountEmailsByUserIdAndStatus(user.getId(), "SENT");
        long countTotalEmails = pageResponse.getTotalElements() + countTotalSentEmails;

        String baseUrl = "/notifications/failed";
        String queryParameters = "";

        int totalPages = pageResponse.getTotalPages();
        long totalElements = pageResponse.getTotalElements();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("user", user);
        modelAndView.addObject("emails", failedEmails);
        modelAndView.addObject("countTotalEmails",countTotalEmails);
        modelAndView.addObject("countSentEmails", countTotalSentEmails);
        modelAndView.addObject("countFailedEmails", failedEmails.size());
        modelAndView.addObject("filter", "FAILED");

        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("queryParameters", queryParameters);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);

        return modelAndView;
    }





}
