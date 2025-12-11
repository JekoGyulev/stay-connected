package com.example.stayconnected.property;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyImageService;
import com.example.stayconnected.property.service.impl.PropertyServiceImpl;
import com.example.stayconnected.review.model.Review;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.utils.exception.PropertyDoesNotExist;
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.property.FilterPropertyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceImplUTest {

    @Mock
    private  PropertyRepository propertyRepository;
    @Mock
    private  LocationService locationService;
    @Mock
    private  PropertyImageService propertyImageService;
    @Mock
    private  ReviewService reviewService;
    @InjectMocks
    private PropertyServiceImpl propertyServiceImpl;


    @Test
    void whenInvokeServiceMethodToGetById_shouldReturnThePropertyByItsId() {

        Property expectedProperty = Property.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .build();


        when(propertyRepository.findById(any())).thenReturn(Optional.of(expectedProperty));


        Property resultProperty = propertyServiceImpl.getById(expectedProperty.getId());


        assertEquals(expectedProperty.getTitle(), resultProperty.getTitle());
        assertNotNull(resultProperty);
    }

    @Test
    void whenInvokeServiceMethodToGetById_andDoesNotExist_shouldThrow() {
        when(propertyRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(PropertyDoesNotExist.class, () ->  propertyServiceImpl.getById(UUID.randomUUID()));
    }


    @Test
    void whenInvokeServiceMethodGetAllProperties_shouldReturnAllProperties() {

        Property property1 = Property.builder().build();
        Property property2 = Property.builder().build();

        List<Property> expected = List.of(property1, property2);

        when(propertyRepository.findAllByOrderByCreateDateDescAverageRatingDesc()).thenReturn(expected);


        List<Property> actual = propertyServiceImpl.getAllProperties();

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToFilterProperties_shouldReturnFilteredPropertiesBasedOnCriteria() {

        String country = "ALL";
        String category = "ALL";

        Location location1 =  Location.builder().country("Bulgaria").build();
        Location location2 =  Location.builder().country("Germany").build();

        Property property1 = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .location(location1)
                .build();

        Property property2 = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.VILLA)
                .location(location2)
                .build();

        List<Property> expected = List.of(property1, property2);

        when(propertyRepository.findAllByOrderByCreateDateDescAverageRatingDesc()).thenReturn(expected);

        List<Property> actual = propertyServiceImpl.getFilteredProperties(new FilterPropertyRequest(category, country));

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToFilterProperties_shouldReturnFilteredPropertiesBasedOnCountry() {

        String country = "Bulgaria";
        String category = "ALL";

        Location location1 =  Location.builder().country("Bulgaria").build();

        Property property1 = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .location(location1)
                .build();

        List<Property> expected = List.of(property1);

        when(propertyRepository.findAllByLocation_CountryOrderByCreateDateDescAverageRatingDesc(location1.getCountry()))
                .thenReturn(expected);

        List<Property> actual = propertyServiceImpl.getFilteredProperties(new FilterPropertyRequest(category, country));

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToFilterProperties_shouldReturnFilteredPropertiesBasedOnCategory() {

        String country = "ALL";
        String category = "APARTMENT";

        Location location1 =  Location.builder().country("Bulgaria").build();

        Property property1 = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .location(location1)
                .build();

        List<Property> expected = List.of(property1);

        when(propertyRepository.findAllByCategoryTypeOrderByCreateDateDescAverageRatingDesc(CategoryType.APARTMENT))
                .thenReturn(expected);

        List<Property> actual = propertyServiceImpl.getFilteredProperties(new FilterPropertyRequest(category, country));

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }


    @Test
    void whenInvokeServiceMethodToFilterProperties_shouldReturnFilteredPropertiesBasedOnCategoryAndCountry() {

        String country = "BULGARIA";
        String category = "APARTMENT";

        Location location1 =  Location.builder().country("Bulgaria").build();

        Property property1 = Property.builder()
                .id(UUID.randomUUID())
                .categoryType(CategoryType.APARTMENT)
                .location(location1)
                .build();

        List<Property> expected = List.of(property1);

        when(propertyRepository.findByCategoryTypeAndLocation_CountryOrderByCreateDateDescAverageRatingDesc(CategoryType.APARTMENT, country))
                .thenReturn(expected);

        List<Property> actual = propertyServiceImpl.getFilteredProperties(new FilterPropertyRequest(category, country));

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToGetPropertiesByOwnerId_shouldReturnAllPropertiesTheOwnerHas() {

        User user = User.builder().id(UUID.randomUUID()).build();

        Property property1 = Property.builder().id(UUID.randomUUID()).owner(user).build();
        Property property2 = Property.builder().id(UUID.randomUUID()).owner(user).build();

        List<Property> expected = List.of(property1, property2);

        when(propertyRepository.findAllByOwnerIdOrderByCreateDateDescAverageRatingDesc(user.getId()))
                .thenReturn(expected);


        List<Property> actual = propertyServiceImpl.getPropertiesByAdminId(user.getId());
        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToGetFeaturedProperties_shouldReturnFeaturedPropertiesOnly() {
        Property property = Property.builder().id(UUID.randomUUID()).averageRating(BigDecimal.valueOf(3.33)).build();
        List<Property> expected = List.of(property);

        when(propertyRepository.findTop4ByOrderByAverageRatingDesc()).thenReturn(List.of(property));

        List<Property> actual = propertyServiceImpl.getFeaturedProperties();

        assertEquals(expected.size(), actual.size());
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void whenInvokeServiceMethodToDeleteProperty_shouldDeleteThePropertyAndItsReviews() {

        Property property = Property.builder().id(UUID.randomUUID()).averageRating(BigDecimal.valueOf(3.33)).build();

        propertyServiceImpl.deleteProperty(property);

        verify(propertyRepository, times(1)).deleteById(property.getId());
        verify(reviewService, times(1)).deleteAllReviewsForProperty(property.getId());
    }


    @Test
    void whenInvokeServiceMethod_shouldEditProperty() {

        User user = User.builder().id(UUID.randomUUID()).build();

        Location location = Location.builder().build();

        Property property = Property.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .description("Description")
                .categoryType(CategoryType.APARTMENT)
                .owner(user)
                .pricePerNight(BigDecimal.valueOf(50))
                .createDate(LocalDateTime.now())
                .amenities(List.of("Amenity"))
                .images(getRandomImage())
                .location(location)
                .averageRating(BigDecimal.valueOf(3.33))
                .build();


        LocationRequest locationDto = new LocationRequest("Country", "City", "Address");

        EditPropertyRequest dto = EditPropertyRequest.builder()
                .title("Title")
                .description("Description")
                .category(CategoryType.APARTMENT)
                .pricePerNight(BigDecimal.valueOf(50))
                .amenities(List.of("Amenity"))
                .location(locationDto)
                .build();

        when(propertyRepository.findById(any())).thenReturn(Optional.of(property));



        propertyServiceImpl.editProperty(property.getId(), dto);

        verify(locationService, times(1)).updateLocation(property.getLocation());
        verify(propertyRepository, times(1)).save(property);

    }
    // TODO: Write unit test for creating property


    @Test
    void whenInvokeServiceMethodToCreateProperty_shouldCreateNewProperty() {

        User user = User.builder().id(UUID.randomUUID()).build();

        MockMultipartFile image1 =
                new MockMultipartFile("file", "image.jpg", "image/jpeg", "fake".getBytes());

        Location location = Location.builder().build();

        LocationRequest locationDto = new LocationRequest("Country", "City", "Address");

        CreatePropertyRequest dto = CreatePropertyRequest.builder()
                .title("Title")
                .description("Description")
                .category(CategoryType.APARTMENT)
                .pricePerNight(BigDecimal.valueOf(50))
                .amenities(List.of("Amenity"))
                .location(locationDto)
                .images(List.of(image1))
                .build();


        when(locationService.createLocation(locationDto)).thenReturn(location);

        Property result = propertyServiceImpl.createProperty(dto, user);


        assertEquals("Title", result.getTitle());
        assertEquals("Description", result.getDescription());
        assertEquals(user,  result.getOwner());
        assertNotNull(result);

        verify(propertyImageService, times(1)).createPropertyImage(any(), any());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }


    public static List<PropertyImage> getRandomImage() {
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setImageURL("/uploads/1763306572664_pool.jpg");
        return List.of(propertyImage);
    }



}
