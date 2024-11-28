package com.example.apiecommerce.domain.cartItem.dto;

import com.example.apiecommerce.domain.cart.Cart;
import com.example.apiecommerce.domain.product.Product;
import jakarta.persistence.*;

public class CartItemDto {
    private Long id;
    private Long cartItemQuantity;
    private Long cartId;
    private Long productId;
}
