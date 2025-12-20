package com.example.stayconnected.web;


import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.service.LocationService;
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
import com.example.stayconnected.web.controller.PropertyController;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.property.FilterPropertyRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

@WebMvcTest(PropertyController.class)
public class PropertyControllerAPITest {

    @MockitoBean
    private PropertyService propertyService;
    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private LocationService locationService;
    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private MockMvc mockMvc;



    @Test
    void sendGetRequestToPropertiesPage_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());

        Location location = getRandomLocation();

        property.setLocation(location);

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getAllProperties()).thenReturn(List.of(property));
        when(locationService.getAllDistinctCountries()).thenReturn(List.of(location.getCountry()));

        MockHttpServletRequestBuilder request =
                get("/properties")
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/properties"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("properties"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attributeExists("filterPropertyRequest"));

        verify(propertyService).getAllProperties();
        verify(locationService).getAllDistinctCountries();
    }


    @Test
    void sendGetRequestToMakeProperty_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));

        when(userService.getUserById(any())).thenReturn(user);

        MockHttpServletRequestBuilder request =
                get("/properties/create")
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/create-property-form"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("createPropertyRequest"));

        verify(userService).getUserById(eq(user.getId()));
    }


    @Test
    void sendPostRequestToCreateAProperty_shouldRedirect_andInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));

        Location location = getRandomLocation();

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "fake-image-data".getBytes()
        );


        LocationRequest locationDto = new LocationRequest(location.getCountry(), location.getCity(), location.getAddress());

        CreatePropertyRequest dto =
                CreatePropertyRequest.builder()
                                .title("Title")
                                        .description("Description is good")
                                                .category(CategoryType.APARTMENT)
                                                        .location(locationDto)
                                                                .pricePerNight(BigDecimal.valueOf(50))
                                                                        .amenities(List.of("Amenities", "Amenities2"))
                                                                                .images(List.of(image))
                        .build();


        Property property = Property.builder().id(UUID.randomUUID()).build();

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.createProperty(any(), any())).thenReturn(property);

        MockHttpServletRequestBuilder request =
                multipart("/properties/create")
                        .file(image)
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("title", dto.getTitle())
                        .param("description", dto.getDescription())
                        .param("category", String.valueOf(dto.getCategory()))
                        .param("location.country", String.valueOf(dto.getLocation().getCountry()))
                        .param("location.city", String.valueOf(dto.getLocation().getCity()))
                        .param("location.address", String.valueOf(dto.getLocation().getAddress()))
                        .param("pricePerNight", String.valueOf(dto.getPricePerNight()))
                        .param("amenities", String.valueOf(dto.getAmenities()));


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/" + property.getId()));



        verify(propertyService).createProperty(any(CreatePropertyRequest.class), eq(user));
    }


    @Test
    void sendPostRequestToCreateAPropertyWithNoImages_shouldNotRedirect_andNotInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));

        Location location = getRandomLocation();


        LocationRequest locationDto = new LocationRequest(location.getCountry(), location.getCity(), location.getAddress());

        CreatePropertyRequest dto =
                CreatePropertyRequest.builder()
                        .title("Title")
                        .description("Description is good")
                        .category(CategoryType.APARTMENT)
                        .location(locationDto)
                        .pricePerNight(BigDecimal.valueOf(50))
                        .amenities(List.of("Amenities", "Amenities2"))
                        .build();


        Property property = Property.builder().id(UUID.randomUUID()).build();

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.createProperty(any(), any())).thenReturn(property);

        MockHttpServletRequestBuilder request =
                post("/properties/create")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("title", dto.getTitle())
                        .param("description", dto.getDescription())
                        .param("category", String.valueOf(dto.getCategory()))
                        .param("location.country", String.valueOf(dto.getLocation().getCountry()))
                        .param("location.city", String.valueOf(dto.getLocation().getCity()))
                        .param("location.address", String.valueOf(dto.getLocation().getAddress()))
                        .param("pricePerNight", String.valueOf(dto.getPricePerNight()))
                        .param("amenities", String.valueOf(dto.getAmenities()));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/create-property-form"));

    }

    @Test
    void sendPostRequestToCreateAPropertyWithInvalidData_shouldNotRedirect_andNotInvokeServiceMethod() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();


        user.setWallet(createRandomWallet(user));

        Location location = getRandomLocation();


        LocationRequest locationDto = new LocationRequest(location.getCountry(), location.getCity(), location.getAddress());

        CreatePropertyRequest dto =
                CreatePropertyRequest.builder()
                        .category(CategoryType.APARTMENT)
                        .location(locationDto)
                        .pricePerNight(BigDecimal.valueOf(50))
                        .amenities(List.of("Amenities", "Amenities2"))
                        .build();


        Property property = Property.builder().id(UUID.randomUUID()).build();

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.createProperty(any(), any())).thenReturn(property);

        MockHttpServletRequestBuilder request =
                post("/properties/create")
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("category", String.valueOf(dto.getCategory()))
                        .param("location.country", String.valueOf(dto.getLocation().getCountry()))
                        .param("location.city", String.valueOf(dto.getLocation().getCity()))
                        .param("location.address", String.valueOf(dto.getLocation().getAddress()))
                        .param("pricePerNight", String.valueOf(dto.getPricePerNight()))
                        .param("amenities", String.valueOf(dto.getAmenities()));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/create-property-form"));

    }



    @Test
    void sendGetRequestToGetAllMyOwnedProperties_shouldReturn200AndView() throws Exception {


        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getPropertiesByAdminId(any())).thenReturn(List.of(property));


        MockHttpServletRequestBuilder request =
                get("/properties/my-properties")
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/manage-properties"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("properties"));


        verify(userService, times(1)).getUserById(any());
        verify(propertyService, times(1)).getPropertiesByAdminId(any());
    }


    @Test
    void sendGetRequestToGetAllMyOwnedProperties_withAMessage_shouldReturn200AndView() throws Exception {


        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());


        String message = "Successfully deleted property!";

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getPropertiesByAdminId(any())).thenReturn(List.of(property));


        MockHttpServletRequestBuilder request =
                get("/properties/my-properties")
                        .with(user(userPrincipal))
                        .param("message", message);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/manage-properties"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("properties"))
                .andExpect(model().attributeExists("message"));


        verify(userService, times(1)).getUserById(any());
        verify(propertyService, times(1)).getPropertiesByAdminId(any());
    }


    @Test
    void sendGetRequestToEditProperty_shouldReturn200AndView() throws Exception {


        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());


        when(propertyService.getById(any())).thenReturn(property);
        when(userService.getUserById(any())).thenReturn(user);



        MockHttpServletRequestBuilder request  =
                get("/properties/{id}/edit", property.getId())
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-edit-form"))
                .andExpect(model().attributeExists("editPropertyRequest"));




        verify(userService, times(1)).getUserById(any());
        verify(propertyService, times(1)).getById(any());
    }



    @Test
    void sendPatchRequestToEditProperty_shouldRedirect_andInvokeServiceMethod() throws Exception {
        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = Property.builder().id(UUID.randomUUID()).build();

        Location location = getRandomLocation();

        LocationRequest locationDto = new LocationRequest(location.getCountry(), location.getCity(), location.getAddress());


        EditPropertyRequest dto =
                EditPropertyRequest.builder()
                        .title("Title")
                        .description("Description is good!")
                        .pricePerNight(BigDecimal.valueOf(50))
                        .amenities(List.of("Amenity1", "Amenity2"))
                        .category(CategoryType.APARTMENT)
                        .location(locationDto)
                        .build();


        MockHttpServletRequestBuilder request =
                patch("/properties/{id}/edit", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("title", dto.getTitle())
                        .param("description", dto.getDescription())
                        .param("pricePerNight", dto.getPricePerNight().toString())
                        .param("category", dto.getCategory().toString())
                        .param("location.country", dto.getLocation().getCountry())
                        .param("location.city", dto.getLocation().getCity())
                        .param("location.address", dto.getLocation().getAddress())
                        .param("amenities", dto.getAmenities().toString());



        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/" + property.getId()  + "?message=Successfully edited property"));




        verify(propertyService, times(1)).editProperty(any(), any(EditPropertyRequest.class));
    }


    @Test
    void sendPatchRequestToEditProperty_shouldReturnSamePage_dueToInvalidPropertyData() throws Exception {
        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = Property.builder().id(UUID.randomUUID()).build();

        Location location = getRandomLocation();

        LocationRequest locationDto = new LocationRequest(location.getCountry(), location.getCity(), location.getAddress());


        EditPropertyRequest dto =
                EditPropertyRequest.builder()
                        .pricePerNight(BigDecimal.valueOf(50))
                        .amenities(List.of("Amenity1", "Amenity2"))
                        .category(CategoryType.APARTMENT)
                        .location(locationDto)
                        .build();


        when(userService.getUserById(any())).thenReturn(user);

        MockHttpServletRequestBuilder request =
                patch("/properties/{id}/edit", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf())
                        .param("pricePerNight", dto.getPricePerNight().toString())
                        .param("category", dto.getCategory().toString())
                        .param("location.country", dto.getLocation().getCountry())
                        .param("location.city", dto.getLocation().getCity())
                        .param("location.address", dto.getLocation().getAddress())
                        .param("amenities", dto.getAmenities().toString());



        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-edit-form"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyId"))
                .andExpect(model().attributeExists("editPropertyRequest"));




        verify(propertyService, never()).editProperty(any(), any(EditPropertyRequest.class));
    }


    @Test
    void sendDeleteRequestToDeleteProperty_shouldRedirect_andInvokeServiceMethod() throws Exception {


        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());


        when(propertyService.getById(any())).thenReturn(property);

        MockHttpServletRequestBuilder request =
                delete("/properties/{id}/delete", property.getId())
                        .with(user(userPrincipal))
                        .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/my-properties?message=Successfully deleted property!"));


        verify(propertyService, times(1)).deleteProperty(eq(property));
    }


    @Test
    void sendGetRequestToGetFilteredProperties_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());

        FilterPropertyRequest dto = new FilterPropertyRequest("ALL", "ALL");


        when(userService.getUserById(any())).thenReturn(user);
        when(locationService.getAllDistinctCountries()).thenReturn(List.of("Country1", "Country2"));
        when(propertyService.getFilteredProperties(any())).thenReturn(List.of(property));


        MockHttpServletRequestBuilder request =
                get("/properties/filter")
                        .with(user(userPrincipal))
                        .param("category", dto.getCategory())
                        .param("country", dto.getCountry());


        mockMvc.perform(request)
                .andExpect(status().isOk())
                        .andExpect(view().name("property/properties"))
                                .andExpect(model().attributeExists("authUser"))
                                .andExpect(model().attributeExists("properties"))
                                .andExpect(model().attributeExists("countries"))
                                .andExpect(model().attributeExists("filterPropertyRequest"));



        ArgumentCaptor<FilterPropertyRequest> captor = ArgumentCaptor.forClass(FilterPropertyRequest.class);

        verify(propertyService, times(1)).getFilteredProperties(captor.capture());

        FilterPropertyRequest captorValue = captor.getValue();

        assertEquals("ALL", captorValue.getCategory());
        assertEquals("ALL", captorValue.getCountry());
    }


    @Test
    void sendGetRequestToGetPropertyDetails_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);
        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .property(property)
                .createdFrom(user)
                .build();


        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(any())).thenReturn(property);
        when(reviewService.getLast5ReviewsForProperty(any())).thenReturn(List.of(review));
        when(reviewService.getAllReviewsByPropertyWithId(any())).thenReturn(List.of(review));


        MockHttpServletRequestBuilder request =
                get("/properties/{id}", property.getId())
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"))
                .andExpect(model().attributeExists("createReviewRequest"))
                .andExpect(model().attributeExists("createReservationRequest"));

    }

    @Test
    void sendGetRequestToGetPropertyDetails_withMessage_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);
        property.setImages(getRandomImage());
        property.setLocation(getRandomLocation());

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .property(property)
                .createdFrom(user)
                .build();

        String message = "Successfully edited property!";


        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(any())).thenReturn(property);
        when(reviewService.getLast5ReviewsForProperty(any())).thenReturn(List.of(review));
        when(reviewService.getAllReviewsByPropertyWithId(any())).thenReturn(List.of(review));


        MockHttpServletRequestBuilder request =
                get("/properties/{id}", property.getId())
                        .with(user(userPrincipal))
                        .param("message", message);


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"))
                .andExpect(model().attributeExists("createReviewRequest"))
                .andExpect(model().attributeExists("createReservationRequest"))
                .andExpect(model().attributeExists("message"));

    }





    @Test
    void sendGetRequestToGetPropertyDetails_with2Images_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getAdminAuthentication();

        User user = User.builder()
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .password(userPrincipal.getPassword())
                .build();

        user.setWallet(createRandomWallet(user));


        Property property = randomProperty(user);

        PropertyImage propertyImage1 = new PropertyImage();
        propertyImage1.setImageURL("/uploads/1763306572664_pool.jpg");


        PropertyImage propertyImage2 = new PropertyImage();
        propertyImage2.setImageURL("/uploads/1763306572664_pool.jpg");

        property.setImages(List.of(propertyImage1, propertyImage2));
        property.setLocation(getRandomLocation());

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .property(property)
                .createdFrom(user)
                .build();


        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getById(any())).thenReturn(property);
        when(reviewService.getLast5ReviewsForProperty(any())).thenReturn(List.of(review));
        when(reviewService.getAllReviewsByPropertyWithId(any())).thenReturn(List.of(review));


        MockHttpServletRequestBuilder request =
                get("/properties/{id}", property.getId())
                        .with(user(userPrincipal));


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("property/property-details"))
                .andExpect(model().attributeExists("authUser"))
                .andExpect(model().attributeExists("propertyOwner"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("gridImages"))
                .andExpect(model().attributeExists("last5Reviews"))
                .andExpect(model().attributeExists("countReviews"))
                .andExpect(model().attributeExists("createReviewRequest"))
                .andExpect(model().attributeExists("createReservationRequest"));

    }










    public static List<PropertyImage> getRandomImage() {
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setImageURL("/uploads/1763306572664_pool.jpg");
        return List.of(propertyImage);
    }

    public static Location getRandomLocation() {
        return Location.builder().city("City").country("Country").address("Address 123").build();
    }

    public static Property randomProperty(User user) {
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .description("Description")
                .averageRating(BigDecimal.valueOf(Math.random()))
                .createDate(LocalDateTime.now())
                .pricePerNight(BigDecimal.valueOf(Math.random()))
                .categoryType(CategoryType.APARTMENT)
                .owner(user)
                .build();

        return property;
    }

    public static UserPrincipal getAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                "zhekogyulev@gmail.com",
                true,
                UserRole.ADMIN
        );
    }

    public static UserPrincipal getNonAdminAuthentication() {
        return new UserPrincipal(
                UUID.randomUUID(),
                "Jeko777",
                "Password123",
                "zhekogyulev@gmail.com",
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
