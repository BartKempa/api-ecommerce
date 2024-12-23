package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDto;
import org.springframework.stereotype.Service;

@Service
class CartDtoMapper {

    static Cart map(CartDto cartDto){
        if (cartDto == null){
            return null;
        }
       Cart cart = new Cart();
       cart.setCreationDate(cartDto.getCreationDate());
       return cart;
    }

    static CartDto map(Cart cart){
        if (cart == null){
            return null;
        }
        return new CartDto(
                cart.getId(),
                cart.getCreationDate()
        );
    }
}
