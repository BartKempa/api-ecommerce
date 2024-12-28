package com.example.apiecommerce.domain.user.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDto {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;
    @NotEmpty
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
