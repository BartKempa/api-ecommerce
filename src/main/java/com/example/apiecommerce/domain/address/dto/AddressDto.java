package com.example.apiecommerce.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for Address")
public class AddressDto {
    @Schema(description = "Address ID")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "Street name")
    private String streetName;

    @NotBlank
    @Size(min = 1, max = 10)
    @Schema(description = "Building number")
    private String buildingNumber;

    @NotBlank
    @Size(min = 1, max = 10)
    @Schema(description = "Apartment number")
    private String apartmentNumber;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "Zip code")
    private String zipCode;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "City name")
    private String city;

    @NotNull
    @Schema(description = "User ID associated with the address")
    private Long userId;

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
