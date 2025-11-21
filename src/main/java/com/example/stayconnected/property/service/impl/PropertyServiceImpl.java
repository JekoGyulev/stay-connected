package com.example.stayconnected.property.service.impl;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyImageService;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.review.service.ReviewService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.utility.exception.PropertyDoesNotExist;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.FilterPropertyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    private final LocationService locationService;
    private final PropertyImageService  propertyImageService;
    private final ReviewService reviewService;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, LocationService locationService, PropertyImageService propertyImageService, ReviewService reviewService) {
        this.propertyRepository = propertyRepository;
        this.locationService = locationService;
        this.propertyImageService = propertyImageService;
        this.reviewService = reviewService;
    }

    @Override
    public Property getById(UUID propertyId) {
        return this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyDoesNotExist("Property with such id [%s] does not exist"
                        .formatted(propertyId)));
    }

    @Override
    public List<Property> getAllProperties() {
        List<Property> properties = this.propertyRepository.findAllByOrderByCreateDateDesc();
        applyAverageRatings(properties);
        return properties;
    }

    @Override
    public Property createProperty(CreatePropertyRequest createPropertyRequest, User owner) {

        Location location = this.locationService.createLocation(createPropertyRequest.getLocation());

        Property property = Property
                .builder()
                .title(createPropertyRequest.getTitle())
                .description(createPropertyRequest.getDescription())
                .categoryType(createPropertyRequest.getCategory())
                .location(location)
                .pricePerNight(createPropertyRequest.getPricePerNight())
                .owner(owner)
                .createDate(LocalDateTime.now())
                .build();

        this.propertyRepository.save(property);

        property.setAmenities(createPropertyRequest.getAmenities());

        for (MultipartFile file : createPropertyRequest.getImages()) {
            if (!file.isEmpty()) {
                this.propertyImageService.createPropertyImage(file, property);
            }
        }

        log.info("Successfully added property with id [%s] and title [%s]"
                .formatted(property.getId(), property.getTitle()));

        return property;
    }

    @Override
    public List<Property> getFilteredProperties(FilterPropertyRequest filterPropertyRequest) {

        List<Property> properties = new ArrayList<>();

        String category = filterPropertyRequest.getCategory();
        String country = filterPropertyRequest.getCountry();

        boolean categoryFilter = category != null && !category.equals("ALL");
        boolean countryFilter = country != null && !country.equals("ALL");

        if (!categoryFilter && !countryFilter) {
           properties = this.propertyRepository.findAllByOrderByCreateDateDesc();
        }

        if (categoryFilter && countryFilter) {
            properties = this.propertyRepository.findByCategoryTypeAndLocation_CountryOrderByCreateDateDesc(
                    CategoryType.valueOf(category), country
            );
        }

        if (categoryFilter) {
            properties = this.propertyRepository
                    .findAllByCategoryTypeOrderByCreateDateDesc(CategoryType.valueOf(category));
        }

        if (countryFilter) {
            properties = this.propertyRepository
                    .findAllByLocation_CountryOrderByCreateDateDesc(country);
        }

        applyAverageRatings(properties);

        return properties;
    }

    private void applyAverageRatings(List<Property> properties) {

        List<UUID> propertyIds = properties.stream()
                .map(Property::getId)
                .toList();

        List<Object[]> averages = this.reviewService.getAverageRatingsForProperties(propertyIds);

        Map<UUID, BigDecimal> averageMap = averages
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> BigDecimal.valueOf((Double) row[1])
                ));

        properties.forEach(property -> {
                        property.setAverageRating(averageMap.getOrDefault(property.getId(), BigDecimal.ZERO));
        });
    }


    // Have at least 1 logging message -> log.info("Successfully done {your operation}")

}
