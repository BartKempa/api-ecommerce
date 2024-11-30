package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
class CartItemDtoMapper {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;


    CartItemDtoMapper(ProductRepository productRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    CartItem map(CartItemDto cartItemDto){
        if (cartItemDto == null){
            return null;
        }
        return new CartItem(
                cartItemDto.getId(),
                cartItemDto.getCartItemQuantity(),
                cartRepository.findById(cartItemDto.getCartId()).orElseThrow(),
                productRepository.findById(cartItemDto.getProductId()).orElseThrow()
        );
    }

    CartItemDto map(CartItem cartItem){
        if (cartItem == null){
            return null;
        }
        return new CartItemDto(
                cartItem.getId(),
                cartItem.getCartItemQuantity(),
                cartItem.getCart().getId(),
                cartItem.getProduct().getId()
        );
    }
}
