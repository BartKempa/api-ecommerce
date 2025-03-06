package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cart.CartRepository;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
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
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldCreateCart() throws Exception {
        //when
        mockMvc.perform(post("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.creationDate").isNotEmpty());

        //then
        User user = userRepository.findByEmail("eighthUser@mail.com").orElseThrow();
        assertNotNull(user.getCart());
        assertTrue(cartRepository.existsById(user.getCart().getId()));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserTriesToCreateSecondCart() throws Exception {
        //given
        boolean cartExists = cartRepository.existsById(
                userRepository.findByEmail("user@mail.com").orElseThrow().getCart().getId());
        assertTrue(cartExists);

        //when & then
        mockMvc.perform(post("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetCartDetails() throws Exception {
        //given & when
        mockMvc.perform(get("/api/v1/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].id").value("3"))
                .andExpect(jsonPath("$.cartItems[0].cartId").value("2"))
                .andExpect(jsonPath("$.cartItems[0].productId").value("2"))
                .andExpect(jsonPath("$.cartItems[0].productName").value("IPA"))
                .andExpect(jsonPath("$.totalCost").value(119.0));
    }

    @Test
    void shouldFailedWhenGettingCartDetailsWithoutAuthorization() throws Exception {
        //given & when
        mockMvc.perform(get("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenUserHasNoCart() throws Exception {
        //given & when
        mockMvc.perform(get("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldDeleteUserCart() throws Exception {
        //given & when
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertNotNull(user.getCart());
        assertTrue(cartRepository.existsById(user.getCart().getId()));

        mockMvc.perform(delete("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertNull(user.getCart());
    }

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenUserHasNoCartAndTryDeleteIt() throws Exception {
        //given & when
        mockMvc.perform(delete("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User does not have a cart"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedUserDeleteCart() throws Exception {
        //given & when
        mockMvc.perform(delete("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldEmptyCart() throws Exception {
        //given & when
        User user = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertNotNull(user.getCart());
        assertFalse(user.getCart().getCartItems().isEmpty());

        mockMvc.perform(post("/api/v1/carts/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenUserHasNoCartAndTryClearIt() throws Exception {
        //given & when
        mockMvc.perform(post("/api/v1/carts/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User does not have a cart"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedAndTryClearCart() throws Exception {
        //given & when
        mockMvc.perform(post("/api/v1/carts/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}