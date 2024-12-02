package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemDtoMapper cartItemDtoMapper;

    public CartItemService(CartItemRepository cartItemRepository, CartItemDtoMapper cartItemDtoMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemDtoMapper = cartItemDtoMapper;
    }

    @Transactional
    public CartItemDto saveCartItem(CartItemDto cartItemDto){
        CartItem cartItemToSave = cartItemDtoMapper.map(cartItemDto);
        CartItem savedCartItem = cartItemRepository.save(cartItemToSave);
        return cartItemDtoMapper.map(savedCartItem);
    }
}
