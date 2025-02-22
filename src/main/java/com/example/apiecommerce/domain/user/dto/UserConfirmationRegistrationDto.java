package com.example.apiecommerce.domain.user.dto;

public class UserConfirmationRegistrationDto {
    private Long id;
    private String email;
    private String firstName;

    private String lastName;
    private String phoneNumber;

    public UserConfirmationRegistrationDto() {
    }

    public UserConfirmationRegistrationDto(Long id, String email, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.email = email;
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
