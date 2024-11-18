package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDtoMapper cartDtoMapper;

    public CartService(CartRepository cartRepository, CartDtoMapper cartDtoMapper) {
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
    }

    @Transactional
    public CartDto addCart(CartDto cartDto){
        Cart cart = CartDtoMapper.map(cartDto);
        cart.setCreationDate(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        return CartDtoMapper.map(savedCart);
    }

    public Optional<CartDto> getCartById(Long id){
        return cartRepository.findById(id)
                .map(CartDtoMapper::map);
    }
}
