package com.example.stayconnected.property;


import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.model.PropertyImage;
import com.example.stayconnected.property.repository.PropertyImageRepository;
import com.example.stayconnected.property.service.impl.PropertyImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyImageServiceImplUTest {

    @Mock
    private PropertyImageRepository propertyImageRepository;
    @InjectMocks
    private PropertyImageServiceImpl propertyImageServiceImpl;


    @Test
    void createPropertyImage_shouldSaveImageAndPersistRecord() throws Exception {

        Property property = Property.builder().id(UUID.randomUUID()).build();

        MockMultipartFile mockFile =
                new MockMultipartFile("file", "image.jpg", "image/jpeg", "test-image".getBytes());

        when(propertyImageRepository.save(any(PropertyImage.class)))
                .thenAnswer(inv -> {
                    PropertyImage img = inv.getArgument(0);
                    img.setId(UUID.randomUUID());
                    return img;
                });

        propertyImageServiceImpl.createPropertyImage(mockFile, property);

        verify(propertyImageRepository, times(1)).save(any(PropertyImage.class));
    }

    @Test
    void createPropertyImage_shouldThrowException_whenTransferFails() throws Exception {

        Property property = Property.builder().id(UUID.randomUUID()).build();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("bad.jpg");

        doThrow(new IOException("Fail")).when(mockFile).transferTo(any(Path.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                propertyImageServiceImpl.createPropertyImage(mockFile, property)
        );

        assertEquals("Failed to save image", ex.getMessage());
    }


}
