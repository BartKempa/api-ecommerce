package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import org.springframework.stereotype.Service;

@Service
public class CartItemFullDtoMapper {
    public CartItemFullDto map(CartItem cartItem) {
        return new CartItemFullDto(
                cartItem.getId(),
                cartItem.getCartItemQuantity(),
                cartItem.getCart().getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getProductName(),
                cartItem.getProduct().getProductPrice()
        );
    }
}
