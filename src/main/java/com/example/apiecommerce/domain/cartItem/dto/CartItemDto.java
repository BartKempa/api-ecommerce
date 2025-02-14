package com.example.apiecommerce.domain.cartItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for Cart Item")
public class CartItemDto {
    @NotNull
    @Min(1)
    @Schema(description = "Cart item ID")
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
