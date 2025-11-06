package com.example.stayconnected.web.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "Password cannot be empty or blank.")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$" , message = "Password is too weak.")
    private String newPassword;
    @NotBlank(message = "Password cannot be empty or blank.")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$" , message = "Password is too weak.")
    private String confirmPassword;
}
