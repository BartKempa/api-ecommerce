package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CartItemDtoMapper {
    private final ProductRepository productRepository;

    private static final Long startCartItemQuantity = 1L;


    CartItemDtoMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CartItem map(CartItemDto cartItemDto){
        if (cartItemDto == null){
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setCartItemQuantity(startCartItemQuantity);
        cartItem.setProduct(productRepository.findById(cartItemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found")));
        return cartItem;
    }

}
