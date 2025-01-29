package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.user.User;
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
    private final CartDetailsDtoMapper cartDetailsDtoMapper;

    public CartService(CartRepository cartRepository, CartDtoMapper cartDtoMapper, UserRepository userRepository, CartDetailsDtoMapper cartDetailsDtoMapper) {
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
        this.userRepository = userRepository;
        this.cartDetailsDtoMapper = cartDetailsDtoMapper;
    }

    @Transactional
    public CartDto createCart(String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getCart() != null) {
            throw new IllegalStateException("User already has a cart");
        }
        Cart cart = new Cart();
        cart.setCreationDate(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);
        user.setCart(savedCart);
        userRepository.save(user);

        return cartDtoMapper.map(savedCart);
    }

    public Optional<CartDetailsDto> getCartDetailsById(Long id){
        return cartRepository.findById(id)
                .map(cartDetailsDtoMapper::map);
    }

    @Transactional
    public void deleteCart(Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Optional<User> userOpt = userRepository.findByCartId(cartId);

        userOpt.ifPresent(
                u -> {
                    u.setCart(null);
                    userRepository.save(u);
                });
        cartRepository.delete(cart);
    }
}
