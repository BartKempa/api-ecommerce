package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cartItem.CartItemFullDtoMapper;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartDetailsDtoMapper {
    private final CartItemFullDtoMapper cartItemFullDtoMapper;

    public CartDetailsDtoMapper(CartItemFullDtoMapper cartItemFullDtoMapper) {
        this.cartItemFullDtoMapper = cartItemFullDtoMapper;
    }


    CartDetailsDto map(Cart cart){
        if (cart == null){
            return null;
        }
        List<CartItemFullDto> cartItems = cart.getCartItems().stream()
                .map(cartItemFullDtoMapper::map)
                .toList();
        double totalCost = cartItems.stream()
                .mapToDouble(c -> c.getProductPrice() * c.getCartItemQuantity())
                .sum();
        return new CartDetailsDto(
                cartItems,
                totalCost
        );
    }
}
