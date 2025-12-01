package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utils.ReservationUtils;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.property.PropertyViewDTO;
import com.example.stayconnected.web.dto.reservation.ReservationViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PropertyService propertyService;


    @Autowired
    public ReservationController(ReservationService reservationService, UserService userService, PropertyService propertyService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/user/table")
    public ModelAndView getReservationsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());


        List<ReservationResponse> reservationResponses = this.reservationService.getReservationsByUserId(userPrincipal.getId());

        List<ReservationViewDTO> reservationViewDTOs = reservationResponses.stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "all");



        return modelAndView;
    }

    @GetMapping("/user/table/booked")
    public ModelAndView getBookedReservationsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<ReservationResponse> bookedReservationsOnly = ReservationUtils.getBookedReservationsOnly(
                this.reservationService.getReservationsByUserId(userPrincipal.getId())
        );

        List<ReservationViewDTO> reservationViewDTOs = bookedReservationsOnly.stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();

        ModelAndView modelAndView = new ModelAndView("/reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "booked");

        return modelAndView;
    }

    @GetMapping("/user/table/cancelled")
    public ModelAndView getCancelledReservationsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        List<ReservationResponse> cancelledReservationsOnly = ReservationUtils.getCancelledReservationsOnly(
                this.reservationService.getReservationsByUserId(userPrincipal.getId())
        );


        List<ReservationViewDTO> reservationViewDTOs = cancelledReservationsOnly.stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();


        ModelAndView modelAndView = new ModelAndView("/reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "cancelled");

        return modelAndView;
    }

    @PatchMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id) {
        this.reservationService.cancel(id);
        return "redirect:/reservations/user/table";
    }

}
