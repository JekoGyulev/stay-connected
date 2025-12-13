package com.example.stayconnected;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.repository.LocationRepository;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.repository.PropertyImageRepository;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EditPropertyITest {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyImageRepository propertyImageRepository;


    @Test
    void editPropertyWithValidData_shouldInvokeServiceMethod_andUpdateTheAlreadyExistingProperty() {

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

        Location location = Location.builder().country("Country").city("City").address("Address").build();

        locationRepository.save(location);

        Property property = Property.builder()
                .title("Title")
                .description("Description")
                .owner(user)
                .pricePerNight(BigDecimal.valueOf(50))
                .categoryType(CategoryType.APARTMENT)
                .amenities(new ArrayList<>(List.of("Amenity1", "Amenity2")))
                .location(location)
                .createDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        propertyRepository.save(property);

        PropertyImage propertyImage = getRandomImage();
        propertyImage.setProperty(property);

        propertyImageRepository.save(propertyImage);


        LocationRequest locationRequest = new LocationRequest("ChangedCountry", "ChangedCity", "Address");

        EditPropertyRequest editPropertyRequest = EditPropertyRequest.builder()
                .title("ChangedTitle")
                .description("ChangedDescription")
                .category(CategoryType.APARTMENT)
                .amenities(new ArrayList<>(List.of("Amenity1", "Amenity2")))
                .pricePerNight(BigDecimal.valueOf(100))
                .location(locationRequest)
                .build();


        propertyService.editProperty(property.getId(), editPropertyRequest);

        Property updatedProperty = propertyService.getById(property.getId());
        Location updatedLocation = locationRepository.findById(location.getId())
                .orElseThrow(() -> new RuntimeException("Location not found"));


        assertNotNull(updatedProperty);
        assertNotNull(updatedLocation);

        assertEquals("ChangedTitle", updatedProperty.getTitle());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), updatedProperty.getPricePerNight());
        assertEquals("ChangedCountry",  updatedLocation.getCountry());
        assertEquals(user.getId(), updatedProperty.getOwner().getId());
    }

    public static PropertyImage getRandomImage() {
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setImageURL("/uploads/1763306572664_pool.jpg");
        return propertyImage;
    }



    
}
