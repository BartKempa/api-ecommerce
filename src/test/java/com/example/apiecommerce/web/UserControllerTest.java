package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.order.OrderRepository;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
import com.example.apiecommerce.domain.user.dto.UserUpdatePasswordDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminGetUserById() throws Exception {
        //given
        long userId = 2L;

        //when
        mockMvc.perform(get("/api/v1/users/user/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.firstName").value("Janek"))
                .andExpect(jsonPath("$.lastName").value("Janecki"))
                .andExpect(jsonPath("$.phoneNumber").value("506111222"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnCorrectErrorResponseForNonExistingUser() throws Exception {
        //given
        long nonExistingUserId = 999L;

        //when
        mockMvc.perform(get("/api/v1/users/user/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldFailedWhenAdminGetUserByIdWithoutAuthentication() throws Exception {
        //given
        long nonExistingUserId = 2;

        //when
        mockMvc.perform(get("/api/v1/users/user/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWhenUserGetUserByIdWithoutAuthorization() throws Exception {
        //given
        long nonExistingUserId = 2;

        //when
        mockMvc.perform(get("/api/v1/users/user/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com")
    void shouldFailWhenUserWithoutRoleTriesToGetUserById() throws Exception {
        //given
        long userId = 2L;

        //when
        mockMvc.perform(get("/api/v1/users/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUpdateUser() throws Exception {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("NewFirstName");
        userUpdateDto.setLastName("NewLastName");
        userUpdateDto.setPhoneNumber("123123123");

        //when
        mockMvc.perform(patch("/api/v1/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNoContent());

        //then
        User updatedUser = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("123123123",updatedUser.getPhoneNumber());
    }

    @Test
    void shouldFailedWhenUserUpdateWithoutAuthentication() throws Exception {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("NewFirstName");

        //when & then
        mockMvc.perform(patch("/api/v1/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUserUpdatePartialDetails() throws Exception {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("NewFirstName");


        //when
        mockMvc.perform(patch("/api/v1/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNoContent());

        //then
        User updatedUser = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("Janecki", updatedUser.getLastName());
        assertEquals("506111222",updatedUser.getPhoneNumber());
    }

    @Test
    @WithMockUser(username = "notexist@mail.com", roles = "USER")
    void shouldReturnNotFoundForNonExistingUser() throws Exception {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("Ghost");

        //when & then
        mockMvc.perform(patch("/api/v1/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldNotChangeAnythingWhenEmptyRequestIsSent() throws Exception {
        //given
        User userBeforeUpdate = userRepository.findByEmail("user@mail.com").orElseThrow();
        UserUpdateDto userUpdateDto = new UserUpdateDto();

        //when
        mockMvc.perform(patch("/api/v1/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNoContent());

        //then
        User updatedUser = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertEquals(userBeforeUpdate.getFirstName(), updatedUser.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), updatedUser.getLastName());
        assertEquals(userBeforeUpdate.getPhoneNumber(), updatedUser.getPhoneNumber());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminDeleteUser() throws Exception {
        //given
        long userId = 2L;
        assertTrue(userRepository.existsById(userId));

        //when
        mockMvc.perform(delete("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailedWhenAdminDeleteNonExistingUser() throws Exception {
        //given
        long nonExistingUserId = 999L;
        assertFalse(userRepository.existsById(nonExistingUserId));

        //when & then
        mockMvc.perform(delete("/api/v1/users/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailedWhenDeleteUserWithoutAuthentication() throws Exception {
        //given
        long nonExistingUserId = 2L;

        //when & then
        mockMvc.perform(delete("/api/v1/users/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWhenDeleteUserWithoutAuthorization() throws Exception {
        //given
        long nonExistingUserId = 2L;

        //when & then
        mockMvc.perform(delete("/api/v1/users/{id}", nonExistingUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldDeleteUserWithOrdersAndAddresses() throws Exception {
        //given
        long userId = 2L;
        assertTrue(userRepository.existsById(userId));
        assertFalse(orderRepository.findAllByUserId(userId).isEmpty());
        assertFalse(addressRepository.findAllByUserId(userId).isEmpty());

        //when
        mockMvc.perform(delete("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(userRepository.existsById(userId));
        assertTrue(orderRepository.findAllByUserId(userId).isEmpty());
        assertTrue(addressRepository.findAllByUserId(userId).isEmpty());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserTriesToDeleteHimself() throws Exception {
        //given
        long selfUserId = 2L;

        //when & then
        mockMvc.perform(delete("/api/v1/users/{id}", selfUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUpdateUserPassword() throws Exception {
        //given
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        userUpdatePasswordDto.setPassword("NewPass123#");

        //when
        mockMvc.perform(patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordDto)))
                .andExpect(status().isNoContent());

        //when
        User updatedUser = userRepository.findByEmail("user@mail.com").orElseThrow();
        assertThat(passwordEncoder.matches("NewPass123#", updatedUser.getPassword())).isTrue();
    }

    @Test
    void shouldFailedWhenUpdateUserPasswordWithoutAuthentication() throws Exception {
        //given
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        userUpdatePasswordDto.setPassword("NewPass123#");

        //when
        mockMvc.perform(patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWhenUpdateUserPasswordAndValidationFailed() throws Exception {
        //given
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        userUpdatePasswordDto.setPassword("pass");

        //when
        mockMvc.perform(patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input"));
    }

    @Test
    @WithMockUser(username = "nonexistent@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        //given
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        userUpdatePasswordDto.setPassword("NewPass123#");

        //when
        mockMvc.perform(patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenPasswordIsNull() throws Exception {
        //given
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();

        //when
        mockMvc.perform(patch("/api/v1/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdatePasswordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input"));
    }
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUserAddresses() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/addresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$").isArray());
    }
    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldGetEmptyListWhenUserHasNoAddresses() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/addresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$").isArray());
    }
    @Test
    void shouldFailWhenUserGetAddressesWithoutAuthentication() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/addresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldNotGetInactiveAddresses() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/addresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.streetName == 'Toruńska')]").doesNotExist());
    }
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetUserOrders() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    @Test
    void shouldFailWhenUserGetOrdersWithoutAuthentication() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldGetEmptyListWhenUserHasNoOrders() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/users/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$").isArray());
    }
}