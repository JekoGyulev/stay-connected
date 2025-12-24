package com.example.stayconnected.web.dto.email;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactHostRequest {

    @NotEmpty(message = "Choose inquiry type")
    private String inquiryType;
    @NotBlank(message = "Subject must be indicated")
    private String subject;
    @NotBlank(message = "Body must be indicated")
    private String body;
    @NotNull
    private UUID propertyId;




}
