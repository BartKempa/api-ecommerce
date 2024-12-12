package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void deleteCartItem(Long cartItemId){
        if (!cartItemRepository.existsById(cartItemId)){
            throw new EntityNotFoundException("CartItem not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    public Optional<CartItemDto> findCartItemById(Long cartItemId){
        if (!cartItemRepository.existsById(cartItemId)){
            return Optional.empty();
        }
        return cartItemRepository.findById(cartItemId).map(cartItemDtoMapper::map);
    }

    @Transactional
    public Optional<CartItemDto> replaceCartItem(Long cartItemId, CartItemDto cartItemDto){
        if (!cartItemRepository.existsById(cartItemId)){
            return Optional.empty();
        }
        cartItemDto.setId(cartItemId);
        CartItem cartItemToUpdate = cartItemDtoMapper.map(cartItemDto);
        CartItem updatedCartItem = cartItemRepository.save(cartItemToUpdate);
        return Optional.of(cartItemDtoMapper.map(updatedCartItem));
    }
}
