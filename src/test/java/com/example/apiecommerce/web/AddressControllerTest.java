package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.address.dto.AddressUpdateDto;
import com.example.apiecommerce.domain.user.User;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldAddAddress() throws Exception {
        //given
        User user = new User();
        user.setId(1L);
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("Marszałkowska");
        addressDto.setBuildingNumber("10");
        addressDto.setApartmentNumber("5A");
        addressDto.setZipCode("00-001");
        addressDto.setCity("Warszawa");
        addressDto.setUserId(1L);

        //when
        //then
        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.streetName").value("Marszałkowska"))
                .andExpect(jsonPath("$.buildingNumber").value("10"))
                .andExpect(jsonPath("$.apartmentNumber").value("5A"))
                .andExpect(jsonPath("$.zipCode").value("00-001"))
                .andExpect(jsonPath("$.city").value("Warszawa"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailToAddAddressWhenStreetNameIsBlank() throws Exception {
        //given
        User user = new User();
        user.setId(1L);
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("");
        addressDto.setBuildingNumber("10");
        addressDto.setApartmentNumber("5A");
        addressDto.setZipCode("00-001");
        addressDto.setCity("Warszawa");
        addressDto.setUserId(1L);

        //when
        //then
        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid input"))
                .andExpect(jsonPath("$.errors.streetName").isArray())
                .andExpect(jsonPath("$.errors.streetName[1]").value("size must be between 2 and 50"))
                .andExpect(jsonPath("$.errors.streetName[0]").value("must not be blank"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldDeleteAddress() throws Exception {
        //given
        long addressIdToDelete = 2L;
        assertTrue(addressRepository.existsById(addressIdToDelete));
        assertTrue(addressRepository.findById(addressIdToDelete).get().isActive());

        //when
        mockMvc.perform(delete("/api/v1/addresses/{id}", addressIdToDelete)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(addressRepository.findById(addressIdToDelete).get().isActive());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenDeleteOtherUserAddress() throws Exception {
        //given
        long otherUserAddressIdToDelete = 5L;
        assertTrue(addressRepository.existsById(otherUserAddressIdToDelete));
        assertTrue(addressRepository.findById(otherUserAddressIdToDelete).get().isActive());

        //when & then
        mockMvc.perform(delete("/api/v1/addresses/{id}", otherUserAddressIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Address belongs to other user"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenAddressNotFound() throws Exception {
        //given
        long nonExistentAddressId = 999L;
        assertFalse(addressRepository.existsById(nonExistentAddressId));

        //when & then
        mockMvc.perform(delete("/api/v1/addresses/{id}", nonExistentAddressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address not found"));
    }

    @Test
    void shouldFailWhenUserNotAuthenticated() throws Exception {
        //given
        long addressId = 2L;
        assertTrue(addressRepository.existsById(addressId));

        //when & then
        mockMvc.perform(delete("/api/v1/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUpdateAddress() throws Exception {
        //given
        long addressId = 2L;
        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();
        addressUpdateDto.setStreetName("Nowa");
        addressUpdateDto.setBuildingNumber("50");
        addressUpdateDto.setApartmentNumber("5");
        addressUpdateDto.setZipCode("30-003");

        //when
        mockMvc.perform(patch("/api/v1/addresses/{id}", addressId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressUpdateDto)))
                .andExpect(status().isNoContent());

        //then
        Address address = addressRepository.findById(addressId).orElseThrow();
        assertEquals("Kraków", address.getCity());
        assertEquals("Nowa", address.getStreetName());
        assertEquals("50", address.getBuildingNumber());
        assertEquals("5", address.getApartmentNumber());
        assertEquals("30-003", address.getZipCode());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenUpdatingAddressOfAnotherUser() throws Exception {
        // given
        long otherUserAddressIdToDelete = 5L;
        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();
        addressUpdateDto.setStreetName("Nowa");

        // when & then
        mockMvc.perform(patch("/api/v1/addresses/{id}", otherUserAddressIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Address belongs to other user, you can not update it"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetAddressById() throws Exception {
        //given
        long addressId = 2L;
        assertTrue(addressRepository.existsById(addressId));

        //when & then
        mockMvc.perform(get("/api/v1/addresses/{id}", addressId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.streetName").value("Krakowska"))
                .andExpect(jsonPath("$.buildingNumber").value("15"))
                .andExpect(jsonPath("$.apartmentNumber").doesNotExist())
                .andExpect(jsonPath("$.zipCode").value("30-002"))
                .andExpect(jsonPath("$.city").value("Kraków"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenAddressDoesNotExist() throws Exception {
        // given
        long nonExistentAddressId = 999L;
        assertFalse(addressRepository.existsById(nonExistentAddressId));

        // when & then
        mockMvc.perform(get("/api/v1/addresses/{id}", nonExistentAddressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address not found"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestWhenAddressBelongsToAnotherUser() throws Exception {
        // given
        long existingAddressId = 1L;
        assertTrue(addressRepository.existsById(existingAddressId));

        // when & then
        mockMvc.perform(get("/api/v1/addresses/{id}", existingAddressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Address belongs to other user, you can not get it"));
    }

}