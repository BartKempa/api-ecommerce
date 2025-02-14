package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import org.springframework.stereotype.Service;

@Service
public class CartDtoMapper {
    private final DateTimeProvider dateTimeProvider;

    public CartDtoMapper(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    public Cart map(CartDto cartDto){
        if (cartDto == null){
            return null;
        }
       Cart cart = new Cart();
       cart.setCreationDate(dateTimeProvider.getCurrentTime());
       return cart;
    }

    CartDto map(Cart cart){
        if (cart == null){
            return null;
        }
        return new CartDto(
                cart.getId(),
                cart.getCreationDate());
    }
}
