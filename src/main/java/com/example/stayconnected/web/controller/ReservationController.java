package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utils.ReservationUtils;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.property.PropertyViewDTO;
import com.example.stayconnected.web.dto.reservation.ReservationViewDTO;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final PropertyService propertyService;
    private final ReviewService reviewService;


    @Autowired
    public ReservationController(ReservationService reservationService, UserService userService, PropertyService propertyService, ReviewService reviewService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.propertyService = propertyService;
        this.reviewService = reviewService;
    }


    @PostMapping("/create/{propertyId}")
    public ModelAndView createReservation(@PathVariable("propertyId") UUID propertyId,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @Valid CreateReservationRequest createReservationRequest,
                                          BindingResult bindingResult) {

        User user = this.userService.getUserById(userPrincipal.getId());

        Property property = this.propertyService.getById(propertyId);
        User propertyOwner = property.getOwner();

        BigDecimal totalPrice = createReservationRequest.getTotalPrice();
        boolean insufficientFunds = user.getWallet().getBalance().compareTo(totalPrice) < 0;

        if (bindingResult.hasErrors() || insufficientFunds || totalPrice == null) {

            List<PropertyImage> gridImages = new ArrayList<>();

            if (property.getImages().size() > 1) {
                gridImages = property.getImages().subList(1,property.getImages().size());
            }

            List<Review> last5Reviews = this.reviewService.getLast5ReviewsForProperty(property.getId());
            int allReviewsCount = this.reviewService.getAllReviewsByPropertyWithId(property.getId()).size();

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("property/property-details");
            modelAndView.addObject("authUser", user);
            modelAndView.addObject("propertyOwner", propertyOwner);
            modelAndView.addObject("property", property);
            modelAndView.addObject("gridImages", gridImages);
            modelAndView.addObject("last5Reviews", last5Reviews);
            modelAndView.addObject("countReviews", allReviewsCount);
            modelAndView.addObject("createReviewRequest", new CreateReviewRequest());

            if (insufficientFunds) {
                modelAndView.addObject("errorMessage", "Insufficient Funds");
            } else if (totalPrice == null) {
                modelAndView.addObject("errorMessage", "Total Price Is Missing");
            }

            return modelAndView;
        }


        createReservationRequest.setUserId(userPrincipal.getId());
        createReservationRequest.setPropertyId(propertyId);

        this.reservationService.create(createReservationRequest, propertyOwner.getId());


        return new ModelAndView("redirect:/reservations/user/table");
    }


    @PatchMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id) {
        this.reservationService.cancel(id);
        return "redirect:/reservations/user/table";
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

}
