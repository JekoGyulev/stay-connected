package com.example.stayconnected.property.service.impl;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.repository.PropertyRepository;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.utility.exception.PropertyDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property getById(UUID propertyId) {
        return this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyDoesNotExist("Property not found"));
    }

    @Override
    public List<Property> getAllProperties() {
        return this.propertyRepository.findAll();
    }

    // Have at least 1 logging message -> log.info("Successfully done {your operation}")

}
