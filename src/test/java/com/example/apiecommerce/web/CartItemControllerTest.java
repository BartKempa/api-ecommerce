package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cartItem.CartItemRepository;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemUpdateQuantityDto;
import com.example.apiecommerce.domain.product.Product;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAddCartItemToUserCart() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);

        //when
        mockMvc.perform(post("/api/v1/cartItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItemQuantity").value(1))
                .andExpect(jsonPath("$.cartId").value(2))
                .andExpect(jsonPath("$.productPrice").value(8.8))
                .andExpect(jsonPath("$.productName").value("Pilsner"));
    }

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldAddCartItemWhenUSerHasNoCart() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);
        User user = userRepository.findByEmail("eighthUser@mail.com").orElseThrow();
        assertNull(user.getCart());

        //when
        mockMvc.perform(post("/api/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItemQuantity").value(1))
                .andExpect(jsonPath("$.productPrice").value(8.8))
                .andExpect(jsonPath("$.productName").value("Pilsner"));

        //then
        assertNotNull(user.getCart());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotLoggedIn() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);

        //when & then
        mockMvc.perform(post("/api/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(999L);

        //when & then
        mockMvc.perform(post("/api/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldDeleteCartItemById() throws Exception {
        //given
        long cartItemId = 1L;
        assertTrue(cartItemRepository.existsById(cartItemId));

        //when
        mockMvc.perform(delete("/api/v1/cartItems/{id}", cartItemId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(cartItemRepository.existsById(cartItemId));
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenCartItemNotExistsAndTryDeleteIt() throws Exception {
        //given
        long nonExistCartItemId = 999L;
        assertFalse(cartItemRepository.existsById(nonExistCartItemId));

        //when & then
        mockMvc.perform(delete("/api/v1/cartItems/{id}", nonExistCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUserTriesToDeleteOtherUsersCartItem() throws Exception {
        //given
        long cartItemId = 1L;
        assertTrue(cartItemRepository.existsById(cartItemId));

        //when & then
        mockMvc.perform(delete("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenProductLinkedToCartItemDoesNotExist() throws Exception {
        //given
        long cartItemId = 1L;
        Product product = productRepository.getProductByCartItemId(cartItemId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.deleteById(product.getId());

        //when & then
        mockMvc.perform(delete("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldUpdateCartItemQuantity() throws Exception {
        //given
        long cartItemId = 1L;
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());

        //when
        mockMvc.perform(patch("/api/v1/cartItems/{id}", cartItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isNoContent());

        //then
        assertEquals(5, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUserTriesToUpdateOtherUsersCartItem() throws Exception {
        //given
        long cartItemId = 1L;
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());


        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenCartItemDoesNotExist() throws Exception {
        // given
        long nonExistentCartItemId = 999L;
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        // when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}", nonExistentCartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenQuantityIsInvalidAndUpdateCartItem() throws Exception {
        // given
        long cartItemId = 1L;
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(-5L);

        // when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticatedAndUpdateCartItem() throws Exception {
        // given
        long cartItemId = 1L;
        CartItemUpdateQuantityDto cartItemUpdateQuantityDto = new CartItemUpdateQuantityDto();
        cartItemUpdateQuantityDto.setCartItemQuantity(5L);

        // when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemUpdateQuantityDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldIncreaseCartItemQuantityByOne() throws Exception {
        //given
        long cartItemId = 1L;
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());

        //when
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/increment", cartItemId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertEquals(3, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUserTriesToIncreaseQuantityByOneOtherUsersCartItem() throws Exception {
        //given
        long cartItemId = 1L;
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());


        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/increment", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticatedAndTriesToIncreaseQuantityByOne() throws Exception {
        //given
        long cartItemId = 1L;

        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/increment", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenCartItemDoesNotExistAndTryToIncreaseQuantityByOneOtherUsersCartItem() throws Exception {
        //given
        long nonExistentCartItemId = 999L;

        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/increment", nonExistentCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReduceCartItemQuantityByOne() throws Exception {
        //given
        long cartItemId = 1L;
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());

        //when
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/decrement", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertEquals(1, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUserTriesToReduceQuantityByOneOtherUsersCartItem() throws Exception {
        //given
        long cartItemId = 1L;
        assertEquals(2, (long) cartItemRepository.findById(1L).orElseThrow().getCartItemQuantity());


        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/decrement", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticatedAndTriesToReduceQuantityByOne() throws Exception {
        //given
        long cartItemId = 1L;

        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/decrement", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenCartItemDoesNotExistAndTryToReduceQuantityByOneOtherUsersCartItem() throws Exception {
        //given
        long nonExistentCartItemId = 999L;

        //when & then
        mockMvc.perform(patch("/api/v1/cartItems/{id}/quantity/decrement", nonExistentCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldGetCartItemById() throws Exception {
        //given
        long cartItemId = 1L;

        //when
        mockMvc.perform(get("/api/v1/cartItems/{id}", cartItemId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemQuantity").value(2))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Pilsner"))
                .andExpect(jsonPath("$.productPrice").value(8.8));
    }

    @Test
    @WithMockUser(username = "seventhUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenGetByIdNotExistSCartItem() throws Exception {
        //given
        long nonExistentCartItemId = 999L;

        //when & then
        mockMvc.perform(get("/api/v1/cartItems/{id}", nonExistentCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUserTriesToGetOtherUsersCartItem() throws Exception {
        //given
        long cartItemId = 1L;

        //when & then
        mockMvc.perform(get("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenUserHasNoCart() throws Exception {
        //given
        long cartItemId = 1L;

        //when & then
        mockMvc.perform(get("/api/v1/cartItems/{id}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}