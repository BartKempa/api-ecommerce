package com.example.apiecommerce.domain.cart.dto;

import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;

import java.util.List;

public class CartDetailsDto {
    private List<CartItemFullDto> cartItems;

    private double totalCost;

    public CartDetailsDto(List<CartItemFullDto> cartItems, double totalCost) {
        this.cartItems = cartItems;
        this.totalCost = totalCost;
    }

    public List<CartItemFullDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemFullDto> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
