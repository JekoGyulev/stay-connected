package com.example.stayconnected.web.controller;


import com.example.stayconnected.notification_preference.client.dto.NotificationPreferenceResponse;
import com.example.stayconnected.notification_preference.service.NotificationPreferenceService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.email.UpsertNotificationPreferenceRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final NotificationPreferenceService  notificationPreferenceService;


    @Autowired
    public SettingsController(UserService userService, NotificationPreferenceService notificationPreferenceService) {
        this.userService = userService;
        this.notificationPreferenceService = notificationPreferenceService;
    }


    @GetMapping("/appearance")
    public ModelAndView showSettingsAppearancePage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        NotificationPreferenceResponse preferenceResponse = this.notificationPreferenceService
                .getNotificationPreferenceByUserId(userPrincipal.getId());

        UpsertNotificationPreferenceRequest preferenceRequest =
                DtoMapper.fromPreferenceResponse(preferenceResponse, userPrincipal.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("settings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("isAppearance", true);
        modelAndView.addObject("preference", preferenceRequest);


        return  modelAndView;
    }

    @GetMapping("/notifications")
    public ModelAndView showSettingsNotificationsPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        NotificationPreferenceResponse preferenceResponse = this.notificationPreferenceService
                .getNotificationPreferenceByUserId(userPrincipal.getId());

        UpsertNotificationPreferenceRequest preferenceRequest =
                DtoMapper.fromPreferenceResponse(preferenceResponse, userPrincipal.getId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("settings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("isNotifications", true);
        modelAndView.addObject("preference", preferenceRequest);

        return   modelAndView;
    }

    @PutMapping("/notifications/preferences/update")
    public String updatePreference(@Valid UpsertNotificationPreferenceRequest request,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "settings";
        }

        redirectAttributes.addFlashAttribute(
                                    "successfulUpdatePreferenceMessage",
                                    "Successfully updated your notification preference"
        );

        this.notificationPreferenceService.upsertNotificationPreference(request);

        return "redirect:/settings/notifications";
    }



    @GetMapping("/about")
    public ModelAndView showSettingsAboutPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        NotificationPreferenceResponse preferenceResponse = this.notificationPreferenceService
                .getNotificationPreferenceByUserId(userPrincipal.getId());

        UpsertNotificationPreferenceRequest preferenceRequest =
                DtoMapper.fromPreferenceResponse(preferenceResponse, userPrincipal.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("settings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("isAbout", true);
        modelAndView.addObject("preference", preferenceRequest);

        return modelAndView;
    }


}
