package com.example.stayconnected;


import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.repository.LocationRepository;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.utils.exception.PropertyDoesNotExist;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreatePropertyITest {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    void createProperty_shouldInvokeServiceMethod_andSaveToDatabase() {

        User user = User.builder()
                .firstName("John")
                .lastName("John")
                .email("John123@gmail.com")
                .password("John123456")
                .username("John17")
                .isActive(true)
                .role(UserRole.ADMIN)
                .registeredAt(LocalDateTime.now())
                .build();

        userRepository.save(user);


        LocationRequest locationRequest = new LocationRequest("Country", "City", "Address");

        MultipartFile image =
                new MockMultipartFile("file", "image.jpg", "image/jpeg", "fake".getBytes());

        CreatePropertyRequest createPropertyRequest = CreatePropertyRequest.builder()
                .title("Title")
                .description("Description is good")
                .category(CategoryType.APARTMENT)
                .pricePerNight(BigDecimal.valueOf(50))
                .location(locationRequest)
                .amenities(List.of("Amenity1", "Amenity2"))
                .images(List.of(image))
                .build();

        Property createdProperty = propertyService.createProperty(createPropertyRequest, user);
        Location createdLocation = createdProperty.getLocation();


        Property savedProperty = propertyRepository.findById(createdProperty.getId())
                .orElseThrow(() -> new PropertyDoesNotExist("Property does not exist"));
        Location savedLocation = locationRepository.findById(createdLocation.getId())
                .orElseThrow(() -> new RuntimeException("Location does not exist"));

        assertNotNull(savedProperty);
        assertNotNull(savedLocation);

        assertEquals(user.getId(), savedProperty.getOwner().getId());

        assertEquals("Title", savedProperty.getTitle());
        assertEquals("Description is good", savedProperty.getDescription());
        assertEquals(1, savedProperty.getImages().size());
        assertEquals("Country", savedLocation.getCountry());
        assertEquals("City", savedLocation.getCity());
    }


}
