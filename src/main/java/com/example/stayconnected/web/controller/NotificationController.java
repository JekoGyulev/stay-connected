package com.example.stayconnected.web.controller;


import com.example.stayconnected.email.client.dto.EmailResponse;
import com.example.stayconnected.email.service.EmailService;
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
    public ModelAndView showEmailsPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        UUID userId = user.getId();

        List<EmailResponse> emailResponses = this.emailService.getAllEmailsByUserId(userId);

        long countSentEmails = this.emailService.getAllSentEmails(emailResponses).size();
        long countFailedEmails = this.emailService.getAllFailedEmails(emailResponses).size();
        long countTotalEmails = countFailedEmails + countSentEmails;

        List<EmailViewDTO> emails = emailResponses.stream()
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

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView showEmailsPageBySearch(@RequestParam(value = "value") String search,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        UUID userId = user.getId();

        List<EmailResponse> allEmails = this.emailService.getAllEmailsByUserId(userId);
        List<EmailResponse> allEmailsBySubjectContaining = this.emailService.getAllEmailsBySubjectContainingAndUserId(search, userId);


        long countSentEmails = this.emailService.getAllSentEmails(allEmails).size();
        long countFailedEmails = this.emailService.getAllFailedEmails(allEmails).size();
        long countTotalEmails = countFailedEmails + countSentEmails;

        List<EmailViewDTO> viewDTOS = allEmailsBySubjectContaining.stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("emails", viewDTOS);
        modelAndView.addObject("countSentEmails", countSentEmails);
        modelAndView.addObject("countFailedEmails", countFailedEmails);
        modelAndView.addObject("countTotalEmails", countTotalEmails);
        modelAndView.addObject("user", user);
        modelAndView.addObject("filter", "ALL");


        return modelAndView;
    }

    @GetMapping("/sent")
    public ModelAndView showSentEmailsPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<EmailResponse> totalEmailResponses = this.emailService.getAllEmailsByUserId(user.getId());

        List<EmailViewDTO> sentEmails = this.emailService.getAllSentEmails(totalEmailResponses)
                .stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("user", user);
        modelAndView.addObject("emails", sentEmails);
        modelAndView.addObject("countTotalEmails", totalEmailResponses.size());
        modelAndView.addObject("countSentEmails", sentEmails.size());
        modelAndView.addObject("countFailedEmails", totalEmailResponses.size() - sentEmails.size());
        modelAndView.addObject("filter", "SENT");

        return modelAndView;
    }

    @GetMapping("/failed")
    public ModelAndView showFailedEmailsPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<EmailResponse> totalEmailResponses = this.emailService.getAllEmailsByUserId(user.getId());

        List<EmailViewDTO> failedEmails = this.emailService.getAllFailedEmails(totalEmailResponses)
                .stream()
                .map(DtoMapper::viewFromEmailResponse)
                .toList();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/email/emails");
        modelAndView.addObject("user", user);
        modelAndView.addObject("emails", failedEmails);
        modelAndView.addObject("countTotalEmails", totalEmailResponses.size());
        modelAndView.addObject("countSentEmails", totalEmailResponses.size() - failedEmails.size());
        modelAndView.addObject("countFailedEmails", failedEmails.size());
        modelAndView.addObject("filter", "FAILED");

        return modelAndView;
    }





}
