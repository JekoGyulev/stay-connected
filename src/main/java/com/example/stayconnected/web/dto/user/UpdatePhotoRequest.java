package com.example.stayconnected.web.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePhotoRequest {
    @NotNull
    @URL(message = "Please enter a valid URL")
    private String photoURL;
}
