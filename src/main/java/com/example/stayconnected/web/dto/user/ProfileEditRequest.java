package com.example.stayconnected.web.dto.user;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

public class ProfileEditRequest {
    @NotBlank(message = "First name cannot be empty or blank")
    @Size(min = 2, message = "First name must be at least 2 characters long.")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty or blank")
    @Size(min = 2, message = "Last name must be at least 2 characters long.")
    private String lastName;
    @NotNull
    @Positive(message = "Please enter age greater than 0")
    @Min(value = 18, message = "Your age must be above 18")
    private int age;
    @Email(message = "Please enter a valid email")
    private String email;
    @NotBlank(message = "Username cannot be empty or blank")
    private String username;
    @URL(message = "Please enter a valid URL")
    private String profilePicture;

    public ProfileEditRequest() {}

    public ProfileEditRequest(String firstName, String lastName, int age, String email, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
