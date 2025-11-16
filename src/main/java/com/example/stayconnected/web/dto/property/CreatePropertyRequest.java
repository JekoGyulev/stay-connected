package com.example.stayconnected.web.dto.property;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.web.dto.location.LocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyRequest {
    @NotBlank(message = "Please write title")
    private String title;
    @NotBlank(message = "Please write description")
    private String description;
    @NotNull(message = "Category type must be selected")
    private CategoryType category;
    @NotNull
    @Valid
    private LocationRequest location;
    @NotNull(message = "Please enter price")
    @Positive(message = "Price must not be negative")
    private BigDecimal pricePerNight;
    @NotEmpty(message = "At least one amenity must be selected")
    private List<String> amenities;
    @NotEmpty(message = "Please upload at least one image")
    private List<MultipartFile> images;
}
