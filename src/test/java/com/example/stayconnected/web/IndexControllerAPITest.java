package com.example.stayconnected.web;

import com.example.stayconnected.config.WebConfiguration;
import com.example.stayconnected.handler.CustomAuthenticationFailureHandler;
import com.example.stayconnected.handler.CustomAuthenticationSuccessHandler;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.security.UserPrincipal;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.service.UserService;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.controller.IndexController;
import com.example.stayconnected.web.dto.location.CityStatsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = IndexController.class)
public class IndexControllerAPITest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private LocationService locationService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getIndexEndpoint_shouldReturn200OkAndIndexView() throws Exception {

        MockHttpServletRequestBuilder request =
                get("/");

        mockMvc.perform(request)
                .andExpect(view().name("index"))
                .andExpect(status().isOk());

    }

    @Test
    void getHomePageEndpoint_shouldReturn200OkAndHomeView() throws Exception {
        User user = randomUser();
        Location location = Location.builder().address("addresss").city("city")
                .country("country").build();
        Property property = randomProperty(user);
        property.setImages(getRandomImage());
        property.setLocation(location);
        CityStatsDTO destination = new CityStatsDTO("city", 4);

        when(userService.getUserById(any())).thenReturn(user);
        when(propertyService.getFeaturedProperties()).thenReturn(List.of(property));
        when(locationService.get4MostPopularDestinations()).thenReturn(List.of(destination));

        MockHttpServletRequestBuilder request = get("/home")
                .with(user(new UserPrincipal(
                                                    user.getId(),
                                                    user.getUsername(),
                                                    user.getPassword(),
                                                    user.isActive(),
                                                    user.getRole()
                                            ))
                );

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("featuredProperties"))
                .andExpect(model().attributeExists("mostPopularDestinations"));
    }


    @Test
    void sendGetRequestToTermsAndConditionsPage_shouldReturn200AndView() throws Exception {

        UserPrincipal userPrincipal = getNonAdminAuthentication();

        MockHttpServletRequestBuilder request = get("/terms-and-condition")
                .with(user(userPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("terms-and-condition"));

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

    public static List<PropertyImage> getRandomImage() {

        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setImageURL("/uploads/1763306572664_pool.jpg");

        return List.of(propertyImage);
    }

    public static Property randomProperty(User user) {
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .description("Description")
                .averageRating(BigDecimal.valueOf(Math.random()))
                .createDate(LocalDateTime.now())
                .pricePerNight(BigDecimal.valueOf(Math.random()))
                .owner(user)
                .build();

        return property;
    }


    public static User randomUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("Jeko777")
                .password("Jeko123123")
                .isActive(true)
                .role(UserRole.USER)
                .firstName("Jeko")
                .lastName("Gyulev")
                .registeredAt(LocalDateTime.now())
                .build();

        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).owner(user).createdAt(LocalDateTime.now())
                .balance(BigDecimal.TEN)
                .build();

        user.setWallet(wallet);

        return user;
    }



















}
