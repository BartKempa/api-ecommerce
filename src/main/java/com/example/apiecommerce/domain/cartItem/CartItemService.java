package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.Cart;
import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemUpdateQuantityDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemDtoMapper cartItemDtoMapper;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemFullDtoMapper cartItemFullDtoMapper;

    public CartItemService(CartItemRepository cartItemRepository, CartItemDtoMapper cartItemDtoMapper, UserRepository userRepository, CartService cartService, CartRepository cartRepository, CartItemFullDtoMapper cartItemFullDtoMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemDtoMapper = cartItemDtoMapper;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.cartItemFullDtoMapper = cartItemFullDtoMapper;
    }


    @Transactional
    public CartItemFullDto addCartItemToCart(String userMail, CartItemDto cartItemDto){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Long cardId = Optional.ofNullable(user.getCart().getId())
                .orElseGet(() -> cartService.createCart(userMail).getId());
        CartItem cartItemToSave = cartItemDtoMapper.map(cartItemDto);
        Cart cart = cartRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cartItemToSave.setCart(cart);
        CartItem savedCartItem = cartItemRepository.save(cartItemToSave);
        return cartItemFullDtoMapper.map(savedCartItem);
    }

    @Transactional
    public void deleteCartItem(Long cartItemId){
        if (!cartItemRepository.existsById(cartItemId)){
            throw new EntityNotFoundException("CartItem not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void updateCartItemQuantity(long cartItemId, CartItemUpdateQuantityDto cartItemUpdateQuantityDto){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        if (cartItemUpdateQuantityDto.getCartItemQuantity() != null){
            cartItem.setCartItemQuantity(cartItemUpdateQuantityDto.getCartItemQuantity());
        }
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void increaseCartItemQuantityByOne(long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        cartItem.setCartItemQuantity(cartItem.getCartItemQuantity() + 1);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void reduceCartItemQuantityByOne(long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        cartItem.setCartItemQuantity(cartItem.getCartItemQuantity() - 1);
        if (cartItem.getCartItemQuantity() <= 0){
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        cartItemRepository.save(cartItem);
    }


/*    public Optional<CartItemDto> findCartItemById(Long cartItemId){
        if (!cartItemRepository.existsById(cartItemId)){
            return Optional.empty();
        }
        return cartItemRepository.findById(cartItemId).map(cartItemDtoMapper::map);
    }

  */
}
