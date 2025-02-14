package com.example.apiecommerce.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Data Transfer Object for DeliveryUpdateDto")
public class DeliveryUpdateDto {
    @Schema(description = "Unique identifier of the delivery", example = "1")
    private Long id;

    @Size(max = 50)
    @Schema(description = "Name of the delivery service", example = "Poczta")
    private String deliveryName;

    @Size(max = 50)
    @Schema(description = "Estimated delivery time", example = "3-4 dni")
    private String deliveryTime;

    @PositiveOrZero
    @Schema(description = "Charge for the delivery service", example = "10.50")
    private Double deliveryCharge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
