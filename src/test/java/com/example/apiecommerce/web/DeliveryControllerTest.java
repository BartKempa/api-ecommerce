package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.delivery.Delivery;
import com.example.apiecommerce.domain.delivery.DeliveryRepository;
import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import com.example.apiecommerce.domain.delivery.dto.DeliveryUpdateDto;
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
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminAddDelivery() throws Exception {
        //given
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryName("Super poczta");
        deliveryDto.setDeliveryTime("7 dni");
        deliveryDto.setDeliveryCharge(7.70);

        //when & then
        mockMvc.perform(post("/api/v1/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deliveryName").value("Super poczta"))
                .andExpect(jsonPath("$.deliveryTime").value("7 dni"))
                .andExpect(jsonPath("$.deliveryCharge").value(7.70));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenAddDeliveryWithoutAuthorization() throws Exception {
        //given
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryName("Super poczta");
        deliveryDto.setDeliveryTime("7 dni");
        deliveryDto.setDeliveryCharge(7.70);

        //when & then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenDeliveryDataIsInvalid() throws Exception {
        //given
        DeliveryDto invalidDeliveryDto = new DeliveryDto();
        invalidDeliveryDto.setDeliveryTime("7 dni");
        invalidDeliveryDto.setDeliveryCharge(7.70);

        //when & then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDeliveryDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnAllActiveDeliveries() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].deliveryName").value("Poczta"))
                .andExpect(jsonPath("$[0].deliveryTime").value("3-5 dni roboczych"))
                .andExpect(jsonPath("$[0].deliveryCharge").value(10.00))
                .andExpect(jsonPath("$[1].deliveryName").value("Kurier UDC"))
                .andExpect(jsonPath("$[2].deliveryName").value("Kurier THL"))
                .andExpect(jsonPath("$[3].deliveryName").value("Odbiór osobisty"));
    }

    @Test
    void shouldFailWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFindDeliveryById() throws Exception {
        //given
        long deliveryId = 1L;
        assertTrue(deliveryRepository.existsById(deliveryId));

        //when & then
        mockMvc.perform(get("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(deliveryId))
                .andExpect(jsonPath("$.deliveryName").value("Poczta"))
                .andExpect(jsonPath("$.deliveryCharge").value(10.00))
                .andExpect(jsonPath("$.deliveryTime").value("3-5 dni roboczych"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundForNonExistingDeliveryId() throws Exception {
        //given
        long nonExistingId = 999L;
        assertFalse(deliveryRepository.existsById(nonExistingId));

        //when & then
        mockMvc.perform(get("/api/v1/deliveries/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        //given
        long deliveryId = 1L;

        //when & then
        mockMvc.perform(get("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestForInvalidId() throws Exception {
        //given
        long invalidId = 0L;

        //when & then
        mockMvc.perform(get("/api/v1/deliveries/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminDeleteDeliveryById() throws Exception {
        //given
        long deliveryId = 1L;
        assertTrue(deliveryRepository.existsById(deliveryId));
        assertTrue(deliveryRepository.findById(deliveryId).get().isActive());

        //when
        mockMvc.perform(delete("/api/v1/deliveries/{id}", deliveryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(deliveryRepository.findById(deliveryId).get().isActive());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserDeleteDeliveryByIdWithoutAuthorization() throws Exception {
        //given
        long deliveryId = 1L;
        assertTrue(deliveryRepository.existsById(deliveryId));
        assertTrue(deliveryRepository.findById(deliveryId).get().isActive());

        //when & then
        mockMvc.perform(delete("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistentDelivery() throws Exception {
        //given
        long nonExistentDeliveryId = 999L;
        assertFalse(deliveryRepository.existsById(nonExistentDeliveryId));

        //when & then
        mockMvc.perform(delete("/api/v1/deliveries/{id}", nonExistentDeliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenDeletingDeliveryWithoutAuthentication() throws Exception {
        //given
        long deliveryId = 1L;
        assertTrue(deliveryRepository.existsById(deliveryId));

        //when & then
        mockMvc.perform(delete("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturn400WhenDeletingDeliveryWithInvalidId() throws Exception {
        //given
        long invalidDeliveryId = 0L;

        //when & then
        mockMvc.perform(delete("/api/v1/deliveries/{id}", invalidDeliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminUpdateDelivery() throws Exception {
        //given
        long deliveryId = 1L;
        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Super poczta");
        deliveryUpdateDto.setDeliveryCharge(16.60);
        assertTrue(deliveryRepository.existsById(deliveryId));

        //when
        mockMvc.perform(patch("/api/v1/deliveries/{id}", deliveryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryUpdateDto)))
                .andExpect(status().isNoContent());

        //then
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        assertEquals("Super poczta", delivery.getDeliveryName());
        assertEquals("3-5 dni roboczych", delivery.getDeliveryTime());
        assertEquals(16.60, delivery.getDeliveryCharge());
    }

    @Test
    void shouldFailWhenUpdateDeliveryWithoutAuthentication() throws Exception {
        //given
        long deliveryId = 1L;
        assertTrue(deliveryRepository.existsById(deliveryId));

        //when & then
        mockMvc.perform(patch("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistingDelivery() throws Exception {
        // given
        long nonExistingId = 999L;
        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Nowa dostawa");

        // when & then
        mockMvc.perform(patch("/api/v1/deliveries/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdatingDeliveryWithInvalidData() throws Exception {
        // given
        long deliveryId = 1L;
        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Nazwa_dostawy_przekraczająca_dopuszczalną_liczbę_znaków_której_nie_powinno_tu_byćXXXXXXXXXXXXXX");
        deliveryUpdateDto.setDeliveryCharge(-5.00);

        // when & then
        mockMvc.perform(patch("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserTriesToUpdateDelivery() throws Exception {
        // given
        long deliveryId = 1L;
        DeliveryUpdateDto deliveryUpdateDto = new DeliveryUpdateDto();
        deliveryUpdateDto.setDeliveryName("Nowa dostawa");

        // when & then
        mockMvc.perform(patch("/api/v1/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryUpdateDto)))
                .andExpect(status().isForbidden());
    }
}