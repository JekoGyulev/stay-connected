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
import com.example.stayconnected.web.dto.location.LocationRequest;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import com.example.stayconnected.web.dto.property.EditPropertyRequest;
import com.example.stayconnected.web.dto.property.FilterPropertyRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



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
    @Cacheable(value = "properties")
    public List<Property> getAllProperties() {
        return this.propertyRepository.findAllByOrderByCreateDateDescAverageRatingDesc();
    }

    @Override
    @CacheEvict(value = "properties", allEntries = true)
    @Transactional
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
                .amenities(createPropertyRequest.getAmenities())
                .createDate(LocalDateTime.now())
                .averageRating(BigDecimal.ZERO)
                .build();

        this.propertyRepository.save(property);

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

        String category = filterPropertyRequest.getCategory();
        String country = filterPropertyRequest.getCountry();

        boolean categoryFilter = category != null && !category.equals("ALL");
        boolean countryFilter = country != null && !country.equals("ALL");

        if (!categoryFilter && !countryFilter) {
           return getAllProperties();
        }

        if (categoryFilter && countryFilter) {
            return this.propertyRepository.findByCategoryTypeAndLocation_CountryOrderByCreateDateDescAverageRatingDesc(
                    CategoryType.valueOf(category), country
            );
        }

        if (categoryFilter) {
            return this.propertyRepository
                    .findAllByCategoryTypeOrderByCreateDateDescAverageRatingDesc(CategoryType.valueOf(category));
        }


        return this.propertyRepository
                .findAllByLocation_CountryOrderByCreateDateDescAverageRatingDesc(country);
    }

    @Override
    @Transactional
    @CacheEvict(value = "properties", allEntries = true)
    public void deleteProperty(Property property) {

        this.reviewService.deleteAllReviewsForProperty(property.getId());

        this.propertyRepository.deleteById(property.getId());

        log.info("Successfully deleted property with id [%s]"
                .formatted(property.getId().toString()));
    }

    @Override
    @Transactional
    @CacheEvict(value = "properties", allEntries = true)
    public void editProperty(UUID id, EditPropertyRequest editPropertyRequest) {

        Property property = this.getById(id);

        LocationRequest locationRequest = editPropertyRequest.getLocation();

        Location location = property.getLocation();
        location.setCountry(locationRequest.getCountry());
        location.setCity(locationRequest.getCity());
        location.setAddress(locationRequest.getAddress());

        this.locationService.updateLocation(location);

        property.setTitle(editPropertyRequest.getTitle());
        property.setDescription(editPropertyRequest.getDescription());
        property.setCategoryType(editPropertyRequest.getCategory());
        property.setLocation(location);
        property.setPricePerNight(editPropertyRequest.getPricePerNight());
        property.setAmenities(editPropertyRequest.getAmenities());

        this.propertyRepository.save(property);

        log.info("Successfully edited property with id [%s] and title [%s]"
                .formatted(property.getId(), property.getTitle()));
    }

    @Override
    public List<Property> getPropertiesByAdminId(UUID adminId) {
        return this.propertyRepository.findAllByOwnerIdOrderByCreateDateDescAverageRatingDesc(adminId);
    }

    @Override
    public List<Property> getFeaturedProperties() {
        return this.propertyRepository.findTop4ByOrderByAverageRatingDesc();
    }

}
