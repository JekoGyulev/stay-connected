package com.example.stayconnected.property.service;

import com.example.stayconnected.property.model.Property;
import org.springframework.web.multipart.MultipartFile;

public interface PropertyImageService {

    void createPropertyImage(MultipartFile image, Property property);
}
