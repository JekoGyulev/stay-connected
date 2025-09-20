package com.example.stayconnected.web.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank(message = "First name cannot be empty or blank.")
    @Size(min = 2, message = "First name must be at least 2 characters long.")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty or blank.")
    @Size(min = 2, message = "Last name must be at least 2 characters long.")
    private String lastName;
    @Min(value = 18, message = "Age must be at least 18.")
    private int age;
    @NotBlank(message = "Email cannot be empty or blank.")
    @Email(message = "Please enter a valid email.")
    private String email;
    @NotBlank(message = "Username cannot be empty or blank.")
    @Size(min = 5, message = "Username must be at least 5 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only consist of letters, numbers and underscores.")
    private String username;
    @NotBlank(message = "Password cannot be empty or blank.")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$" , message = "Password is too weak.")
    private String password;

    public RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, int age, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
