package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDtoMapper cartDtoMapper;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartDtoMapper cartDtoMapper, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartDto createCart(String userMail){
        Cart cart = new Cart();
        cart.setCreationDate(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .setCart(savedCart);
        return cartDtoMapper.map(savedCart);
    }

/*    @Transactional
    public CartDto createCart(CartDto cartDto, String userMail){
        Cart cart = cartDtoMapper.map(cartDto);
        cart.setCreationDate(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .setCart(savedCart);
        return cartDtoMapper.map(savedCart);
    }*/

    public Optional<CartDto> getCartById(Long id){
        return cartRepository.findById(id)
                .map(cartDtoMapper::map);
    }

    @Transactional
    public void deleteCart(Long cartId){
        if (!cartRepository.existsById(cartId)){
            throw  new EntityNotFoundException("Cart not found");
        }
        cartRepository.deleteById(cartId);
    }
}
