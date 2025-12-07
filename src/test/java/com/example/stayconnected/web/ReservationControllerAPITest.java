package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.reservation.client.dto.ReservationResponse;
import com.example.stayconnected.reservation.service.ReservationService;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.utils.ReservationUtils;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.controller.ReservationController;
import com.example.stayconnected.web.dto.DtoMapper;
import com.example.stayconnected.web.dto.property.PropertyViewDTO;
import com.example.stayconnected.web.dto.reservation.ReservationViewDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerAPITest {

    @MockitoBean
    private ReservationService reservationService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PropertyService propertyService;
    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void sendPatchMappingToCancelReservation_shouldRedirect_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        UUID reservationId = UUID.randomUUID();

        MockHttpServletRequestBuilder request =
                patch("/reservations/{reservationId}/cancel", reservationId)
                        .with(user(userPrincipal))
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations/user/table"));

        verify(reservationService).cancel(eq(reservationId), eq(userPrincipal.getId()));
    }


    @Test
    void sendGetRequestToGetAllBookedReservationsInATable_shouldReturn200OkAndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));

        Property property1 = Property.builder().id(UUID.randomUUID()).build();
        Property property2 = Property.builder().id(UUID.randomUUID()).build();

        ReservationResponse response1 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(50))
                .status("BOOKED")
                .propertyId(property1.getId())
                .build();

        ReservationResponse response2 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(100))
                .status("BOOKED")
                .propertyId(property2.getId())
                .build();

        List<ReservationResponse> responses = List.of(response1, response2);

        PropertyViewDTO propView1 = PropertyViewDTO.builder()
                .photoUrl("photo1.jpg")
                .build();

        PropertyViewDTO propView2 = PropertyViewDTO.builder()
                .photoUrl("photo2.jpg")
                .build();

        ReservationViewDTO view1 = ReservationViewDTO.builder()
                .propertyViewDTO(propView1)
                .reservationId(UUID.randomUUID())
                .build();

        ReservationViewDTO view2 = ReservationViewDTO.builder()
                .propertyViewDTO(propView2)
                .reservationId(UUID.randomUUID())
                .build();

        when(reservationService.getReservationsByUserId(user.getId())).thenReturn(responses);
        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(property1.getId())).thenReturn(property1);
        when(propertyService.getById(property2.getId())).thenReturn(property2);

        try (MockedStatic<ReservationUtils> mockedUtils = mockStatic(ReservationUtils.class);
             MockedStatic<DtoMapper> mockedMapper = mockStatic(DtoMapper.class)) {

            mockedUtils.when(() -> ReservationUtils.getBookedReservationsOnly(responses))
                    .thenReturn(responses);

            mockedMapper.when(() -> DtoMapper.viewFromProperty(property1)).thenReturn(propView1);
            mockedMapper.when(() -> DtoMapper.viewFromProperty(property2)).thenReturn(propView2);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView1, response1))
                    .thenReturn(view1);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView2, response2))
                    .thenReturn(view2);



            MockHttpServletRequestBuilder request = get("/reservations/user/table/booked")
                    .with(user(userPrincipal));


            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(view().name("/reservation/user-reservations"))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attributeExists("reservations"))
                    .andExpect(model().attributeExists("filter"));
        }


    }

    @Test
    void sendGetRequestToGetAllCancelledReservationsInATable_shouldReturn200OkAndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));

        Property property1 = Property.builder().id(UUID.randomUUID()).build();
        Property property2 = Property.builder().id(UUID.randomUUID()).build();

        ReservationResponse response1 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(50))
                .status("CANCELLED")
                .propertyId(property1.getId())
                .build();

        ReservationResponse response2 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(100))
                .status("CANCELLED")
                .propertyId(property2.getId())
                .build();

        List<ReservationResponse> responses = List.of(response1, response2);

        PropertyViewDTO propView1 = PropertyViewDTO.builder()
                .photoUrl("photo1.jpg")
                .build();

        PropertyViewDTO propView2 = PropertyViewDTO.builder()
                .photoUrl("photo2.jpg")
                .build();

        ReservationViewDTO view1 = ReservationViewDTO.builder()
                .propertyViewDTO(propView1)
                .reservationId(UUID.randomUUID())
                .build();

        ReservationViewDTO view2 = ReservationViewDTO.builder()
                .propertyViewDTO(propView2)
                .reservationId(UUID.randomUUID())
                .build();

        when(reservationService.getReservationsByUserId(user.getId())).thenReturn(responses);
        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(property1.getId())).thenReturn(property1);
        when(propertyService.getById(property2.getId())).thenReturn(property2);

        try (MockedStatic<ReservationUtils> mockedUtils = mockStatic(ReservationUtils.class);
             MockedStatic<DtoMapper> mockedMapper = mockStatic(DtoMapper.class)) {

            mockedUtils.when(() -> ReservationUtils.getBookedReservationsOnly(responses))
                    .thenReturn(responses);

            mockedMapper.when(() -> DtoMapper.viewFromProperty(property1)).thenReturn(propView1);
            mockedMapper.when(() -> DtoMapper.viewFromProperty(property2)).thenReturn(propView2);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView1, response1))
                    .thenReturn(view1);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView2, response2))
                    .thenReturn(view2);



            MockHttpServletRequestBuilder request = get("/reservations/user/table/cancelled")
                    .with(user(userPrincipal));


            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(view().name("/reservation/user-reservations"))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attributeExists("reservations"))
                    .andExpect(model().attributeExists("filter"));
        }


    }

    @Test
    void sendGetRequestToUsersReservationsTable_shouldReturn200OkAndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();



        user.setWallet(createRandomWallet(user));

        Property property1 = Property.builder().id(UUID.randomUUID()).build();
        Property property2 = Property.builder().id(UUID.randomUUID()).build();

        ReservationResponse response1 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(50))
                .status("BOOKED")
                .propertyId(property1.getId())
                .build();

        ReservationResponse response2 = ReservationResponse.builder()
                .totalPrice(BigDecimal.valueOf(100))
                .status("CANCELLED")
                .propertyId(property2.getId())
                .build();

        List<ReservationResponse> responses = List.of(response1, response2);

        PropertyViewDTO propView1 = PropertyViewDTO.builder()
                .photoUrl("photo1.jpg")
                .build();

        PropertyViewDTO propView2 = PropertyViewDTO.builder()
                .photoUrl("photo2.jpg")
                .build();

        ReservationViewDTO view1 = ReservationViewDTO.builder()
                .propertyViewDTO(propView1)
                .reservationId(UUID.randomUUID())
                .build();

        ReservationViewDTO view2 = ReservationViewDTO.builder()
                .propertyViewDTO(propView2)
                .reservationId(UUID.randomUUID())
                .build();

        when(reservationService.getReservationsByUserId(user.getId())).thenReturn(responses);
        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(property1.getId())).thenReturn(property1);
        when(propertyService.getById(property2.getId())).thenReturn(property2);

        try (MockedStatic<ReservationUtils> mockedUtils = mockStatic(ReservationUtils.class);
             MockedStatic<DtoMapper> mockedMapper = mockStatic(DtoMapper.class)) {

            mockedUtils.when(() -> ReservationUtils.getBookedReservationsOnly(responses))
                    .thenReturn(responses);

            mockedMapper.when(() -> DtoMapper.viewFromProperty(property1)).thenReturn(propView1);
            mockedMapper.when(() -> DtoMapper.viewFromProperty(property2)).thenReturn(propView2);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView1, response1))
                    .thenReturn(view1);

            mockedMapper.when(() -> DtoMapper.fromPropertyViewAndResponse(propView2, response2))
                    .thenReturn(view2);


            MockHttpServletRequestBuilder request = get("/reservations/user/table")
                    .with(user(userPrincipal));


            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(view().name("reservation/user-reservations"))
                    .andExpect(model().attributeExists("filter"))
                    .andExpect(model().attributeExists("reservations"))
                    .andExpect(model().attributeExists("user"));



        }

    }



    @Test
    void createReservation_shouldRedirectToReservationsTable_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));

        user.getWallet().setBalance(BigDecimal.valueOf(3000));

        Location location = Location.builder().city("city").address("address").country("country").build();

        PropertyImage image = PropertyImage.builder().imageURL("image1.jpg").build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .owner(user)
                .location(location)
                .images(List.of(image))
                .build();


        CreateReservationRequest dto = CreateReservationRequest.builder()
                .userId(user.getId())
                .propertyId(property.getId())
                .totalPrice(BigDecimal.valueOf(100))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();



        when(userService.getUserById(user.getId())).thenReturn(user);
        when(propertyService.getById(property.getId())).thenReturn(property);





        MockHttpServletRequestBuilder request =
                post("/reservations/create/{propertyId}", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("userId", dto.getUserId().toString())
                        .param("propertyId", dto.getPropertyId().toString())
                        .param("totalPrice", dto.getTotalPrice().toString())
                        .param("startDate", dto.getStartDate().toString())
                        .param("endDate", dto.getEndDate().toString());



        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations/user/table"));


        ArgumentCaptor<CreateReservationRequest> captor = ArgumentCaptor.forClass(CreateReservationRequest.class);

        verify(reservationService).create( captor.capture(), eq(user.getId()));

        assertEquals(dto.getUserId(), captor.getValue().getUserId());
        assertEquals(dto.getPropertyId(), captor.getValue().getPropertyId());
    }

    @Test
    void createReservationButTheBalanceIsNotEnough_shouldReturnSamePage_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));

        user.getWallet().setBalance(BigDecimal.valueOf(20));

        Location location = Location.builder().city("city").address("address").country("country").build();

        PropertyImage image = PropertyImage.builder().imageURL("image1.jpg").build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .owner(user)
                .location(location)
                .images(List.of(image))
                .build();


        CreateReservationRequest dto = CreateReservationRequest.builder()
                .userId(user.getId())
                .propertyId(property.getId())
                .totalPrice(BigDecimal.valueOf(100))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();



        when(userService.getUserById(user.getId())).thenReturn(user);
        when(propertyService.getById(property.getId())).thenReturn(property);





        MockHttpServletRequestBuilder request =
                post("/reservations/create/{propertyId}", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("userId", dto.getUserId().toString())
                        .param("propertyId", dto.getPropertyId().toString())
                        .param("totalPrice", dto.getTotalPrice().toString())
                        .param("startDate", dto.getStartDate().toString())
                        .param("endDate", dto.getEndDate().toString());



        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"))
                .andExpect(model().attributeExists("createReviewRequest"));
    }


    @Test
    void createReservationButThePriceOfReservationIsNull_shouldReturnSamePage_andInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));
        user.getWallet().setBalance(BigDecimal.valueOf(20));

        Location location = Location.builder()
                .city("city").address("address").country("country").build();

        PropertyImage image = PropertyImage.builder().imageURL("image1.jpg").build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .owner(user)
                .location(location)
                .images(List.of(image))
                .build();

        CreateReservationRequest dto = CreateReservationRequest.builder()
                .userId(user.getId())
                .propertyId(property.getId())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(propertyService.getById(property.getId())).thenReturn(property);

        // REQUIRED
        when(reviewService.getLast5ReviewsForProperty(property.getId()))
                .thenReturn(List.of());

        when(reviewService.getAllReviewsByPropertyWithId(property.getId()))
                .thenReturn(List.of());

        MockHttpServletRequestBuilder request =
                post("/reservations/create/{propertyId}", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("userId", dto.getUserId().toString())
                        .param("propertyId", dto.getPropertyId().toString())
                        .param("totalPrice", "0.00")
                        .param("startDate", dto.getStartDate().toString())
                        .param("endDate", dto.getEndDate().toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"))
                .andExpect(model().attributeExists("createReviewRequest"));
    }












    public static UserPrincipal getAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                true,
                UserRole.ADMIN
        );
    }

    public static UserPrincipal getNonAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                true,
                UserRole.USER
        );
    }

    public static Wallet createRandomWallet(User user) {

        return Wallet.builder().id(UUID.randomUUID())
                .owner(user)
                .build();
    }

}
