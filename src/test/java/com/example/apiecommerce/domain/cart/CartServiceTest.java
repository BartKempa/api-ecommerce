package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.cartItem.CartItem;
import com.example.apiecommerce.domain.cartItem.CartItemRepository;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepositoryMock;

    @Mock
    private CartDtoMapper cartDtoMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CartDetailsDtoMapper cartDetailsDtoMapperMock;

    @Mock
    private CartItemRepository cartItemRepositoryMock;

    @Mock
    private DateTimeProvider dateTimeProviderMock;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepositoryMock, cartDtoMapperMock, userRepositoryMock, cartDetailsDtoMapperMock, cartItemRepositoryMock, dateTimeProviderMock);
    }

    @Test
    void shouldCreateCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        CartDto cartDto = new CartDto();
        cartDto.setCreationDate(now);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(dateTimeProviderMock.getCurrentTime()).thenReturn(now);
        Mockito.when(cartRepositoryMock.save(any(Cart.class))).thenReturn(cart);
        Mockito.when(cartDtoMapperMock.map(cart)).thenReturn(cartDto);

        //when
        CartDto resultCartDto = cartService.createCart("test@mail.com");
        //then
        ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
        Mockito.verify(cartRepositoryMock).save(cartArgumentCaptor.capture());
        assertEquals(now, resultCartDto.getCreationDate());
        Mockito.verify(userRepositoryMock).save(user);
        Cart capturedCart = cartArgumentCaptor.getValue();
        assertEquals(now, capturedCart.getCreationDate());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(new Cart());

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        IllegalStateException exc = assertThrows(IllegalStateException.class,
                () -> cartService.createCart("test@mail.com"));

        //then
        assertEquals("User already has a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        String nonExistingEmail = "notExist@mail.com";
        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartService.createCart(nonExistingEmail));

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldFindUserCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        user.setCart(cart);

        CartItemFullDto cartItemFullDto1 = new CartItemFullDto(1L, 2L, 1L, 5L, "Pillsner", 10.50);
        CartItemFullDto cartItemFullDto2 = new CartItemFullDto(2L, 1L, 1L, 4L, "Lech", 8.80);
        List<CartItemFullDto> cartItemFullDtoList = List.of(cartItemFullDto1, cartItemFullDto2);
        CartDetailsDto cartDetailsDto = new CartDetailsDto(cartItemFullDtoList, 29.90);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartDetailsDtoMapperMock.map(any(Cart.class))).thenReturn(cartDetailsDto);

        //when
        CartDetailsDto cartDetailsDtoResult = cartService.findUserCart("test@mail.com").orElseThrow();

        //then
        assertNotNull(cartDetailsDtoResult);
        assertEquals(29.90, cartDetailsDtoResult.getTotalCost());
        assertEquals(2, cartDetailsDtoResult.getCartItems().size());
        assertEquals("Pillsner", cartDetailsDtoResult.getCartItems().get(0).getProductName());
        Mockito.verify(userRepositoryMock).findByEmail("test@mail.com");
        Mockito.verify(cartDetailsDtoMapperMock).map(any(Cart.class));
    }

    @Test
    void shouldThrowExceptionWhenTryFindNotExistUserCart() {
        //given
        String nonExistingEmail = "notExist@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when
        Optional<CartDetailsDto> userCart = cartService.findUserCart("notExist@mail.com");

        //then
        assertEquals(Optional.empty(), userCart);
        Mockito.verify(userRepositoryMock).findByEmail(nonExistingEmail);
    }

    @Test
    void shouldThrowExceptionWhenTryFindNUserCartWhenCartNotExists() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        
        //when
        Optional<CartDetailsDto> userCartResult = cartService.findUserCart("test@mail.com");
        //then
        assertTrue(userCartResult.isEmpty());
    }

    @Test
    void shouldDeleteCartWithoutIncreasingStock() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(userRepositoryMock.findByCartId(1L)).thenReturn(Optional.of(user));

        //when
        cartService.deleteCartWithoutIncreasingStock("test@mail.com");

        //then
        ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
        Mockito.verify(cartRepositoryMock).delete(cartArgumentCaptor.capture());
        assertNull(user.getCart());
        Mockito.verify(userRepositoryMock).save(user);
    }

    @Test
    void shouldThrowExceptionWhenDeleteCartWithoutIncreasingStockAndUserNotExists() {
        //given
        String nonExistingEmail = "notExist@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartService.deleteCartWithoutIncreasingStock("notExist@mail.com"));


        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeleteCartWithoutIncreasingStockAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartService.deleteCartWithoutIncreasingStock("test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldDeleteCartWithIncreasingStock() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        user.setCart(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.doNothing().when(cartItemRepositoryMock).deleteAll(any());

        //when
        cartService.deleteCartWithIncreasingStock("test@mail.com");

        //then
        ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
        Mockito.verify(cartRepositoryMock).delete(cartArgumentCaptor.capture());
        assertNull(user.getCart());

        ArgumentCaptor<Collection<CartItem>> cartItemArgumentCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cartItemRepositoryMock).deleteAll(cartItemArgumentCaptor.capture());
        assertFalse(cartItemArgumentCaptor.getValue().isEmpty());
    }


    @Test
    void shouldThrowExceptionWhenDeleteCartWithIncreasingStockAndUserNotExists() {
        //given
        String nonExistingEmail = "notExist@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartService.deleteCartWithIncreasingStock("notExist@mail.com"));


        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeleteCartWithIncreasingStockAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartService.deleteCartWithIncreasingStock("test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }


    @Test
    void shouldClearCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart();
        cart.setCreationDate(now);
        cart.setId(1L);
        user.setCart(cart);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        cartService.clearCart("test@mail.com");

        //then
        Mockito.verify(cartItemRepositoryMock).deleteAllByCart_Id(eq(cart.getId()));
    }

    @Test
    void shouldThrowExceptionWhenClearCartAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        IllegalStateException exc = assertThrows(IllegalStateException.class,
                () -> cartService.clearCart("test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenClearCartAndUserNotExists() {
        //given
        String nonExistingEmail = "notExist@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartService.clearCart("notExist@mail.com"));


        //then
        assertEquals("User not found", exc.getMessage());
    }


}