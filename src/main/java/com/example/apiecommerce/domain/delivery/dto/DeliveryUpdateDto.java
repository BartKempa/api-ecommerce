package com.example.apiecommerce.domain.delivery.dto;

import jakarta.validation.constraints.*;

public class DeliveryUpdateDto {
    private Long id;
    @Size(max = 50)
    private String deliveryName;
    @Size(max = 50)
    private String deliveryTime;
    @PositiveOrZero
    private Double deliveryCharge;

    public Long getId() {
        return id;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }
}
