package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.controller.ReviewController;
import com.example.stayconnected.web.dto.review.CreateReviewRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerAPITest {

    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PropertyService propertyService;
    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    MockMvc mockMvc;

    @Test
    void sendDeleteRequestForReview_shouldRedirectSuccessfully_andInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        Property property = Property.builder().id(UUID.randomUUID()).build();


        Review review = Review.builder()
                .id(UUID.randomUUID())
                .property(property)
                .build();


        when(reviewService.getReviewById(any())).thenReturn(review);

        MockHttpServletRequestBuilder request =
                delete("/reviews/{id}/delete", review.getId())
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/" + property.getId()));

        verify(reviewService).deleteReview(review);
    }

    @Test
    void sendPostRequestToCreateReviewWithError_shouldReturn_andNotInvokeMethod() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder().id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Location location = Location.builder().id(UUID.randomUUID())
                .country("Country")
                .city("City")
                .build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .location(location)
                .categoryType(CategoryType.APARTMENT)
                .build();

        PropertyImage image = PropertyImage.builder().id(UUID.randomUUID())
                .imageURL("someImageUrl")
                .property(property)
                .build();

        property.setImages(List.of(image));


        Review review = Review.builder()
                .id(UUID.randomUUID())
                .rating(5)
                .comment("The comment is for testing")
                .property(property)
                .createdFrom(user)
                .build();



        CreateReviewRequest dto = CreateReviewRequest.builder()
                .rating(5)
                .comment(null)
                .build();




        List<Review> last5Reviews = List.of(review);

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(any())).thenReturn(property);
        when(reviewService.getLast5ReviewsForProperty(any())).thenReturn(last5Reviews);
        when(reviewService.getAllReviewsByPropertyWithId(any())).thenReturn(last5Reviews);


        MockHttpServletRequestBuilder request =
                post("/reviews/create/{propertyId}", review.getId())
                        .param("comment", dto.getComment())
                        .param("rating", String.valueOf(dto.getRating()))
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("/property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"));

        verify(reviewService, never()).addReview(user.getId(), property.getId(), dto);
    }

    @Test
    void sendPostRequestToCreateReviewWithErrorAnd2PropertyImages_shouldReturn_andNotInvokeMethod() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder().id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Location location = Location.builder().id(UUID.randomUUID())
                .country("Country")
                .city("City")
                .build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .location(location)
                .categoryType(CategoryType.APARTMENT)
                .build();

        PropertyImage image = PropertyImage.builder().id(UUID.randomUUID())
                .imageURL("someImageUrl")
                .property(property)
                .build();

        PropertyImage image2 = PropertyImage.builder().id(UUID.randomUUID())
                .imageURL("randomURLImage")
                .property(property)
                .build();


        property.setImages(List.of(image, image2));


        Review review = Review.builder()
                .id(UUID.randomUUID())
                .rating(5)
                .comment("The comment is for testing")
                .property(property)
                .createdFrom(user)
                .build();



        CreateReviewRequest dto = CreateReviewRequest.builder()
                .rating(5)
                .comment(null)
                .build();




        List<Review> last5Reviews = List.of(review);

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(any())).thenReturn(property);
        when(reviewService.getLast5ReviewsForProperty(any())).thenReturn(last5Reviews);
        when(reviewService.getAllReviewsByPropertyWithId(any())).thenReturn(last5Reviews);


        MockHttpServletRequestBuilder request =
                post("/reviews/create/{propertyId}", review.getId())
                        .param("comment", dto.getComment())
                        .param("rating", String.valueOf(dto.getRating()))
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("/property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"));

        verify(reviewService, never()).addReview(user.getId(), property.getId(), dto);
    }


    // Now do test for when everything is correct and a review is posted

    @Test
    void sendPostRequestToCreateReviewWithCorrectData_shouldRedirect_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));

        UUID propertyId = UUID.randomUUID();


        CreateReviewRequest dto = CreateReviewRequest.builder()
                .rating(5)
                .comment("The comment is for testing")
                .build();


        MockHttpServletRequestBuilder request =
                post("/reviews/create/{propertyId}", propertyId)
                        .param("comment", dto.getComment())
                        .param("rating", String.valueOf(dto.getRating()))
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/" + propertyId));


        ArgumentCaptor<CreateReviewRequest> captor  = ArgumentCaptor.forClass(CreateReviewRequest.class);

        verify(reviewService).addReview(eq(user.getId()), eq(propertyId) ,captor.capture());

        assertEquals(dto.getRating(), captor.getValue().getRating());
        assertEquals(dto.getComment(), captor.getValue().getComment());
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
