package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.cartItem.CartItemRepository;
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
    private final CartItemRepository cartItemRepository;
    private final DateTimeProvider dateTimeProvider;


    public CartService(CartRepository cartRepository, CartDtoMapper cartDtoMapper, UserRepository userRepository, CartDetailsDtoMapper cartDetailsDtoMapper, CartItemRepository cartItemRepository, DateTimeProvider dateTimeProvider) {
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
        this.userRepository = userRepository;
        this.cartDetailsDtoMapper = cartDetailsDtoMapper;
        this.cartItemRepository = cartItemRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Transactional
    public CartDto createCart(String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getCart() != null) {
            throw new IllegalStateException("User already has a cart");
        }
        Cart cart = new Cart();
        cart.setCreationDate(dateTimeProvider.getCurrentTime());
        Cart savedCart = cartRepository.save(cart);
        user.setCart(savedCart);
        userRepository.save(user);
        return cartDtoMapper.map(savedCart);
    }

    public Optional<CartDetailsDto> findUserCart(String userMail) {
        return userRepository.findByEmail(userMail)
                .flatMap(user -> Optional.ofNullable(user.getCart()))
                .map(cartDetailsDtoMapper::map);
    }

    @Transactional
    public void deleteCartWithoutIncreasingStock(String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getCart() == null) {
            throw new EntityNotFoundException("User does not have a cart");
        }
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Optional<User> userOpt = userRepository.findByCartId(user.getCart().getId());
        userOpt.ifPresent(
                u -> {
                    u.setCart(null);
                    userRepository.save(u);
                });
        cartRepository.delete(cart);
    }

    @Transactional
    public void deleteCartWithIncreasingStock(String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getCart() == null) {
            throw new EntityNotFoundException("User does not have a cart");
        }
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.deleteAll(cart.getCartItems());
        user.setCart(null);
        cartRepository.delete(cart);
    }

    @Transactional
    public void clearCart(String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Cart cart = Optional.ofNullable(user.getCart())
                .orElseThrow(() -> new IllegalStateException("User does not have a cart"));
        cartItemRepository.deleteAllByCart_Id(cart.getId());
    }
}
