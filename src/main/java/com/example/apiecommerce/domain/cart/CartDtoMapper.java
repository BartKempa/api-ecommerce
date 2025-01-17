package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CartDtoMapper {


    public Cart map(CartDto cartDto){
        if (cartDto == null){
            return null;
        }
       Cart cart = new Cart();
       cart.setCreationDate(cartDto.getCreationDate());
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
