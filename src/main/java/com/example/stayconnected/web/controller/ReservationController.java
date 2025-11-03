package com.example.stayconnected.web.controller;

import com.example.stayconnected.reservation.service.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import java.util.UUID;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/user/table")
    public ModelAndView getReservationsByUser() {

        /*
            Going to use @AuthenticationPrincipal and UserPrincipal
            to get the logged in user id which we will put in the getAllReservationsByUser

         */

        //List<ReservationInfoDTO> reservationsByUser = this.reservationService.getAllReservationsByUser("the id");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation/user-reservations");
        //modelAndView.addObject("reservationsByUser", reservationsByUser);

        return modelAndView;
    }



    // POST /reservations -> Create reservation



    @GetMapping("/{id}/details")
    public ModelAndView getReservationDetails(@PathVariable UUID id) {

        // Fetch the reservation by id

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation/reservation-details");

        // Add reservation as object to the ModelAndView, as well as the property

        return modelAndView;
    }

    // POST Mapping -> Create reservation

    @PutMapping("/{id}/cancel")
    public String modelAndView(@PathVariable UUID id) {

        // Get reservation by id
        // Change status to CANCELLED

        return "redirect:/reservations";
    }

}
