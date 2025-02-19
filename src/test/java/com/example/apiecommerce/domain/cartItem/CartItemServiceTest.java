package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.Cart;
import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemUpdateQuantityDto;
import com.example.apiecommerce.domain.product.Product;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.product.ProductService;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepositoryMock;
    @Mock
    private CartItemDtoMapper cartItemDtoMapperMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CartRepository cartRepositoryMock;
    @Mock
    private CartItemFullDtoMapper cartItemFullDtoMapperCart;
    @Mock
    private ProductService productServiceMock;
    @Mock
    private ProductRepository productRepositoryMock;
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
    cartItemService = new CartItemService(cartItemRepositoryMock, cartItemDtoMapperMock, userRepositoryMock, cartServiceMock, cartRepositoryMock, cartItemFullDtoMapperCart, productServiceMock, productRepositoryMock);
    }

    @Test
    void shouldAddCartItemToCart() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Cart cart = new Cart();
        cart.setId(1L);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        cartItem.setProduct(product);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        CartItemFullDto cartItemFullDto = new CartItemFullDto(1L, 2L, 1L, 1L, "Pillsner", 10.80);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartItemDtoMapperMock.map(cartItemDto)).thenReturn(cartItem);
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.save(Mockito.any())).thenReturn(cartItem);
        Mockito.when(cartItemFullDtoMapperCart.map(cartItem)).thenReturn(cartItemFullDto);

        //when
        CartItemFullDto cartItemFullDtoResult = cartItemService.addCartItemToCart("test@mail.com", cartItemDto);

        //then
        Mockito.verify(productServiceMock).reduceProductQuantityInDbByOne(1L);
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        Mockito.verify(cartItemRepositoryMock).save(cartItemArgumentCaptor.capture());
        CartItem captorValue = cartItemArgumentCaptor.getValue();
        assertEquals("Pillsner", captorValue.getProduct().getProductName());
        assertEquals(10.80, captorValue.getProduct().getProductPrice());

        assertEquals("Pillsner", cartItemFullDtoResult.getProductName());
        assertEquals(10.80, cartItemFullDtoResult.getProductPrice());
        assertEquals(1L, cartItemFullDtoResult.getCartId());
        assertEquals(2L, cartItemFullDtoResult.getCartItemQuantity());
    }

    @Test
    void shouldThrowExceptionWhenAddCartItemToCartAndUserNotExist() {
        //given
        String nonExistingUser = "nonExistUser@mail.com";
        CartItemDto cartItemDto = new CartItemDto();

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.addCartItemToCart("nonExistUser@mail.com", cartItemDto));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldCreateNewCartWhenUserHasNoCart() {
        //given
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        CartDto newCartDto = new CartDto();
        newCartDto.setId(2L);

        Cart newCart = new Cart();
        newCart.setId(2L);


        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(newCart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartServiceMock.createCart("test@mail.com")).thenAnswer(invocation -> {
            user.setCart(newCart);
            return newCartDto;
        });
        Mockito.when(cartRepositoryMock.findById(newCartDto.getId())).thenReturn(Optional.of(newCart));
        Mockito.when(cartItemDtoMapperMock.map(cartItemDto)).thenReturn(cartItem);
        Mockito.when(cartItemRepositoryMock.save(Mockito.any())).thenReturn(cartItem);

        //when
        cartItemService.addCartItemToCart("test@mail.com", cartItemDto);

        //then
        Mockito.verify(cartServiceMock).createCart("test@mail.com");
    }

    @Test
    void shouldDeleteCartItem() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        cartItem.setProduct(product);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));

        //when
        cartItemService.deleteCartItem(1L, "test@mail.com");

        //then
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(cartItemRepositoryMock).deleteById(argumentCaptor.capture());
        Mockito.verify(productServiceMock).updateProductQuantityInDb(1L, -2L);
    }

    @Test
    void shouldThrowExceptionWhenDeleteCartItemAndUserNotExist() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.deleteCartItem(1L, "nonExistUser@mail.com"));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeleteCartItemNotInUserCart() {
        // given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new HashSet<>());

        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));

        // when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.deleteCartItem(1L, "test@mail.com"));

        // then
        assertTrue(exc.getMessage().contains("This cart item does not belong to your cart"));
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotExistCartItem() {
        // given
        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(Mockito.any())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.deleteCartItem(1L, "test@mail.com"));

        // then
        assertTrue(exc.getMessage().contains("Cart item not found"));
    }

    @Test
    void shouldThrowExceptionWhenProductNotExistAndTryDeleteCartItem() {
        // given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(Set.of(cartItem));
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.deleteCartItem(1L, "test@mail.com"));

        // then
        assertTrue(exc.getMessage().contains("Product not found"));
    }

    @Test
    void shouldUpdateCartItemQuantity() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(10L);
        cartItem.setProduct(product);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));

        //when
        cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "test@mail.com");

        //then
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        Mockito.verify(cartItemRepositoryMock).save(cartItemArgumentCaptor.capture());
        CartItem captorValue = cartItemArgumentCaptor.getValue();
        assertEquals(5L, captorValue.getCartItemQuantity());
        Mockito.verify(productServiceMock).updateProductQuantityInDb(1L, 3L);
    }

    @Test
    void shouldThrowExceptionWhenUpdateCartItemQuantityAndUserNotExist() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "nonExistUser@mail.com"));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserHasNoCart() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCartItemNotFound() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "test@mail.com"));

        //then
        assertEquals("Cart item not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForCartItem() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "test@mail.com"));

        //then
        assertEquals("Product not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughProductQuantity() {
        //given
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(15L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(10L);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));
        Mockito.doThrow(new IllegalArgumentException("Not enough quantity in stock"))
                .when(productServiceMock).updateProductQuantityInDb(1L, 13L);

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.updateCartItemQuantity(1L, cartItemUpdateQuantityDto, "test@mail.com"));

        //then
        assertEquals("Not enough quantity in stock", exc.getMessage());
    }

    @Test
    void shouldIncreaseCartItemQuantityByOne() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(10L);
        cartItem.setProduct(product);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));

        //when
        cartItemService.increaseCartItemQuantityByOne(1L, "test@mail.com");

        //then
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        Mockito.verify(cartItemRepositoryMock).save(cartItemArgumentCaptor.capture());
        CartItem captorValue = cartItemArgumentCaptor.getValue();
        assertEquals(3L, captorValue.getCartItemQuantity());
        Mockito.verify(productServiceMock).reduceProductQuantityInDbByOne(1L);
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndUserNotExist() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L, "nonExistUser@mail.com"));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L,"test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndCartItemNotFound() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Cart item not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndProductNotFoundForCartItem() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Product not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndProductQuantityIsZero() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(0L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));
        Mockito.doThrow(new IllegalArgumentException("Product is unavailable"))
                .when(productServiceMock).reduceProductQuantityInDbByOne(1L);

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Product is unavailable", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseCartItemQuantityByOneAndCartItemDoesNotBelongToUser() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(2L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        Cart userCart = new Cart();
        userCart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(userCart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(userCart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.increaseCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("This cart item does not belong to your cart", exc.getMessage());
    }

    @Test
    void shouldReduceCartItemQuantityByOne() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(10L);
        cartItem.setProduct(product);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));

        //when
        cartItemService.reduceCartItemQuantityByOne(1L, "test@mail.com");

        //then
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        Mockito.verify(cartItemRepositoryMock).save(cartItemArgumentCaptor.capture());
        CartItem captorValue = cartItemArgumentCaptor.getValue();
        assertEquals(1L, captorValue.getCartItemQuantity());
        Mockito.verify(productServiceMock).increaseProductQuantityInDbByOne(1L);
        Mockito.verify(cartItemRepositoryMock).save(cartItem);
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndUserNotExist() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L, "nonExistUser@mail.com"));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L,"test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndCartItemNotFound() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Cart item not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndProductNotFoundForCartItem() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Product not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndCartItemDoesNotBelongToUser() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(2L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        Cart userCart = new Cart();
        userCart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(userCart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(userCart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("This cart item does not belong to your cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReduceCartItemQuantityByOneAndCartItemIsOne() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(1L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(10L);
        cartItem.setProduct(product);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepositoryMock.getProductByCartItemId(1L)).thenReturn(Optional.of(product));

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.reduceCartItemQuantityByOne(1L, "test@mail.com"));

        //then
        assertEquals("Quantity cannot be less than 1", exc.getMessage());
    }

    @Test
    void shouldFindCartItemById() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartItemQuantity(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(cart);
        Product product = new Product();
        product.setProductName("Pillsner");
        product.setProductPrice(10.80);
        product.setId(1L);
        product.setProductQuantity(10L);
        cartItem.setProduct(product);
        CartItemFullDto cartItemFullDto = new CartItemFullDto(1L, 2L, 1L, 1L, "Pillsner", 10.80);


        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(cartItemFullDtoMapperCart.map(Mockito.any())).thenReturn(cartItemFullDto);

        //when
        CartItemFullDto cartItemFullDtoResult = cartItemService.findCartItemById(1L, "test@mail.com").orElseThrow();

        //then
        assertEquals("Pillsner", cartItemFullDtoResult.getProductName());
        assertEquals(2L, cartItemFullDtoResult.getCartItemQuantity());
        assertEquals(1L, cartItemFullDtoResult.getCartId());
        assertEquals(1L, cartItemFullDtoResult.getProductId());
        assertEquals(10.80, cartItemFullDtoResult.getProductPrice());
    }

    @Test
    void shouldThrowExceptionWhenFindCartItemByIdAndUserNotExist() {
        //given
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.findCartItemById(1L, "nonExistUser@mail.com"));

        //then
        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFindCartItemByIdAndUserHasNoCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setCart(null);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.findCartItemById(1L,"test@mail.com"));

        //then
        assertEquals("User does not have a cart", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFindCartItemByIdAndCartItemNotFound() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(cartRepositoryMock.findById(1L)).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.findCartItemById(1L, "test@mail.com"));

        //then
        assertEquals("Cart item not found", exc.getMessage());
    }
}