package com.example.apiecommerce.domain.cartItem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CartItemUpdateQuantityDto {
    @NotNull
    @PositiveOrZero
    private long cartItemQuantity;

    public Long getCartItemQuantity() {
        return cartItemQuantity;
    }

    public void setCartItemQuantity(Long cartItemQuantity) {
        this.cartItemQuantity = cartItemQuantity;
    }
}
