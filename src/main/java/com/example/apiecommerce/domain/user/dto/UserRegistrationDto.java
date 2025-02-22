package com.example.apiecommerce.domain.user.dto;

import com.example.apiecommerce.domain.user.validation.PasswordCriteria;
import com.example.apiecommerce.domain.user.validation.UniqueEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Data Transfer Object for User")
public class UserRegistrationDto {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    @Schema(description = "Email address", example = "email@email.com")
    @NotBlank
    @Email
    @UniqueEmail
    private String email;
    @Schema(description = "Password to the account", example = "Password123#")
    @NotBlank
    @PasswordCriteria
    private String password;
    @Schema(description = "User firstname", example = "Bartek")
    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;
    @Schema(description = "User lastname", example = "Kowalski")
    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;
    @Schema(description = "User phone number", example = "Kowalski")
    @NotEmpty
    private String phoneNumber;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(Long id, String email, String password, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

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
