package com.example.apiecommerce.domain.orderItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Data Transfer Object for OrderItem")
public class OrderItemDto {
    @Schema(description = "Unique identifier of the orderItem", example = "1")
    private Long id;
    @Schema(description = "Quantity of the orderItem", example = "5")
    @NotNull
    @PositiveOrZero
    private long orderItemQuantity;
    @Schema(description = "ID of the order", example = "2")
    @NotNull
    @Min(1)
    private long orderId;
    @Schema(description = "ID of the product", example = "2")
    @NotNull
    @Min(1)
    private long productId;
    @Schema(description = "Name of the product", example = "Pilsner")
    @NotBlank(message = "Product name cannot be empty")
    private String productName;
    @Schema(description = "Price of the product", example = "10.99")
    @NotNull
    @Positive
    private double productPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderItemQuantity() {
        return orderItemQuantity;
    }

    public void setOrderItemQuantity(Long orderItemQuantity) {
        this.orderItemQuantity = orderItemQuantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
}
