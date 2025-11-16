package com.example.stayconnected.property.service;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.web.dto.property.CreatePropertyRequest;

import java.util.List;
import java.util.UUID;

public interface PropertyService {

    Property getById(UUID propertyId);

    List<Property> getAllProperties();

    Property createProperty(CreatePropertyRequest createPropertyRequest, User owner);

    // Update property
    // Delete property
}
