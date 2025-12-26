package com.example.stayconnected.property.service.impl;

import com.example.stayconnected.aop.annotations.LogCreation;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.repository.PropertyImageRepository;
import com.example.stayconnected.property.service.PropertyImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Slf4j
public class PropertyImageServiceImpl implements PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;

    @Autowired
    public PropertyImageServiceImpl(PropertyImageRepository propertyImageRepository) {
        this.propertyImageRepository = propertyImageRepository;
    }

    @Override
    @LogCreation(entity = "property image")
    public PropertyImage createPropertyImage(MultipartFile image, Property property) {

        try {

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            image.transferTo(filePath);

            String imageUrl = "/uploads/" + fileName;

            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageURL(imageUrl);
            propertyImage.setProperty(property);

            this.propertyImageRepository.save(propertyImage);

            return propertyImage;

        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
