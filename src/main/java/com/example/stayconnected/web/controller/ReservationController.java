package com.example.stayconnected.web.controller;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.PageResponse;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
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


    @PatchMapping("/{reservationId}/cancel")
    public String cancel(@PathVariable UUID reservationId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        this.reservationService.cancel(reservationId, userPrincipal.getId());
        return "redirect:/reservations/user/table";
    }

    @GetMapping("/user/table")
    public ModelAndView getReservationsByUser(
                                                @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());


        PageResponse<ReservationResponse> reservationResponses = this.reservationService
                .getReservationsByUserId(userPrincipal.getId(), pageNumber, pageSize);

        List<ReservationViewDTO> reservationViewDTOs = reservationResponses.getContent().stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();


        String baseUrl = "/reservations/user/table";

        int totalPages = reservationResponses.getTotalPages();
        long totalElements = reservationResponses.getTotalElements();




        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "all");
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("baseUrl", baseUrl);



        return modelAndView;
    }

    @GetMapping("/user/table/booked")
    public ModelAndView getBookedReservationsByUser(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        PageResponse<ReservationResponse> bookedReservationsPageOnly = this.reservationService.getReservationsByUserIdAndReservationStatus(
                user.getId(),
                "BOOKED",
                pageNumber,
                pageSize
        );

        List<ReservationViewDTO> reservationViewDTOs = bookedReservationsPageOnly.getContent().stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();


        String baseUrl = "/reservations/user/table/booked";

        int totalPages = bookedReservationsPageOnly.getTotalPages();
        long totalElements = bookedReservationsPageOnly.getTotalElements();


        ModelAndView modelAndView = new ModelAndView("/reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "booked");
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("baseUrl", baseUrl);

        return modelAndView;
    }

    @GetMapping("/user/table/cancelled")
    public ModelAndView getCancelledReservationsByUser(
                                                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                        @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = this.userService.getUserById(userPrincipal.getId());

        PageResponse<ReservationResponse> cancelledReservationsOnly = this.reservationService
                .getReservationsByUserIdAndReservationStatus(
                    user.getId(),
                    "CANCELLED",
                    pageNumber,
                    pageSize
                );


        List<ReservationViewDTO> reservationViewDTOs = cancelledReservationsOnly.getContent().stream()
                .map(response -> {
                    Property property = this.propertyService.getById(response.getPropertyId());
                    PropertyViewDTO propertyViewDTO = DtoMapper.viewFromProperty(property);

                    return DtoMapper.fromPropertyViewAndResponse(propertyViewDTO, response);
                })
                .toList();


        int totalPages = cancelledReservationsOnly.getTotalPages();
        long totalElements = cancelledReservationsOnly.getTotalElements();

        String baseUrl = "/reservations/user/table/cancelled";


        ModelAndView modelAndView = new ModelAndView("/reservation/user-reservations");
        modelAndView.addObject("user", user);
        modelAndView.addObject("reservations", reservationViewDTOs);
        modelAndView.addObject("filter", "cancelled");
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("totalElements", totalElements);
        modelAndView.addObject("currentPage", pageNumber);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("baseUrl", baseUrl);

        return modelAndView;
    }

}
