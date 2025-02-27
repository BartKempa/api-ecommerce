package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void deleteAddress() {
    }

    @Test
    void updateAddress() {
    }

    @Test
    void getAddressById() {
    }
}