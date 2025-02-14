package com.example.apiecommerce.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


@Schema(description = "Data Transfer Object for Order")
public class OrderDto {

    @Schema(description = "Unique identifier of the order", example = "123")
    private Long id;

    @Schema(description = "Date and time when the order was created", example = "2024-02-13T14:30:00")
    private LocalDateTime creationDate;

    @Schema(description = "ID of the address associated with the order", example = "1")
    @NotNull
    @Min(1)
    private long addressId;

    @Schema(description = "ID of the selected delivery method", example = "2")
    @NotNull
    @Min(1)
    private long deliveryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }
}
