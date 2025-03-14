package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    void deleteUser() {

    }

    @Test
    void updateUserPassword() {
    }

    @Test
    void getUserAddresses() {
    }

    @Test
    void getUserOrders() {
    }
}