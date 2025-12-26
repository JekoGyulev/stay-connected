package com.example.stayconnected.property.service;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import org.springframework.web.multipart.MultipartFile;

public interface PropertyImageService {

    PropertyImage createPropertyImage(MultipartFile image, Property property);
}
