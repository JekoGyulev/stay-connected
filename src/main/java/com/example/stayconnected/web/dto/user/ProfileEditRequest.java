package com.example.stayconnected.web.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEditRequest {
    @NotBlank(message = "First name cannot be empty or blank")
    @Size(min = 2, message = "First name must be at least 2 characters long.")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty or blank")
    @Size(min = 2, message = "Last name must be at least 2 characters long.")
    private String lastName;
    @Email(message = "Please enter a valid email")
    @NotBlank(message = "Email cannot be empty or blank.")
    private String email;
    @NotBlank(message = "Username cannot be empty or blank.")
    @Size(min = 5, message = "Username must be at least 5 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only consist of letters, numbers and underscores.")
    private String username;
}
