package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemDtoMapper {
    private final ProductRepository productRepository;

    private static final Long startCartItemQuantity = 1L;


    CartItemDtoMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    CartItem map(CartItemDto cartItemDto){
        if (cartItemDto == null){
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setCartItemQuantity(startCartItemQuantity);
        cartItem.setProduct(productRepository.findById(cartItemDto.getProductId()).orElseThrow());
        return cartItem;
    }

}
