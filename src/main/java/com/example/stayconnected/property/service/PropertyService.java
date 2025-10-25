package com.example.stayconnected.property.service;

import com.example.stayconnected.property.model.Property;

import java.util.List;
import java.util.UUID;

public interface PropertyService {

    Property getById(UUID propertyId);

    List<Property> getAllProperties();

    // Create property
    // Update property
    // Delete property
}
