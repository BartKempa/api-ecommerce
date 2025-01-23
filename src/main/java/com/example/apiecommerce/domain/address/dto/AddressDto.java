package com.example.apiecommerce.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddressDto {
    private Long id;
    @NotBlank
    @Size(min = 2, max = 50)
    private String streetName;
    @NotBlank
    @Size(min = 1, max = 10)
    private String buildingNumber;
    @NotBlank
    @Size(min = 1, max = 10)
    private String apartmentNumber;
    @NotBlank
    @Size(max = 10)
    private String zipCode;
    @NotBlank
    @Size(min = 2, max = 50)
    private String city;
    @NotNull
    private Long userId;

    public AddressDto() {
    }

    public AddressDto(Long id, String streetName, String buildingNumber, String apartmentNumber, String zipCode, String city, Long userId) {
        this.id = id;
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.apartmentNumber = apartmentNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
