package com.example.stayconnected.property.service.impl;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyImageService;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.utility.exception.PropertyDoesNotExist;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    private final LocationService locationService;
    private final PropertyImageService  propertyImageService;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, LocationService locationService, PropertyImageService propertyImageService) {
        this.propertyRepository = propertyRepository;
        this.locationService = locationService;
        this.propertyImageService = propertyImageService;
    }

    @Override
    public Property getById(UUID propertyId) {
        return this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyDoesNotExist("Property with such id [%s] does not exist"
                        .formatted(propertyId)));
    }

    @Override
    public List<Property> getAllProperties() {
        return this.propertyRepository.findAll();
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


    // Have at least 1 logging message -> log.info("Successfully done {your operation}")

}
