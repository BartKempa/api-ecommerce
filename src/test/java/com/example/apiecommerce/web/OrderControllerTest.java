package com.example.apiecommerce.web;


import com.example.apiecommerce.domain.order.dto.OrderDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldCreateOrderBasedOnUserCart() throws Exception {
        //given
        OrderDto orderDto = new OrderDto();
        orderDto.setAddressId(2L);
        orderDto.setDeliveryId(1L);

        //when
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.orderTotalPrice").value(129.0))
                .andExpect(jsonPath("$.userFirstName").value("Janek"))
                .andExpect(jsonPath("$.orderItems[0].productName").value("IPA"))
                .andExpect(jsonPath("$.orderItems[1].productName").value("Chardonnay"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWHenCreateOrderBasedOnCartAndOtherUserAddress() throws Exception {
        //given
        OrderDto orderDto = new OrderDto();
        orderDto.setAddressId(4L);
        orderDto.setDeliveryId(1L);

        //when
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Address not belong to the specified user"));
    }


        @Test
        @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
        void shouldReturnNotFound_whenCartDoesNotExist() throws Exception {
            //given
            OrderDto orderDto = new OrderDto();
            orderDto.setAddressId(4L);
            orderDto.setDeliveryId(1L);

            //when & then
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "user@mail.com", roles = "USER")
        void shouldReturnNotFound_whenAddressDoesNotExist() throws Exception {
            // given
            OrderDto orderDto = new OrderDto();
            orderDto.setAddressId(999L);
            orderDto.setDeliveryId(1L);


            // when & then
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "user@mail.com", roles = "USER")
        void shouldReturnNotFound_whenDeliveryMethodDoesNotExist() throws Exception {
            // given
            OrderDto orderDto = new OrderDto();
            orderDto.setAddressId(2L);
            orderDto.setDeliveryId(999L);

            // when & then
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnForbidden_whenUserIsNotAuthenticated() throws Exception {
            // given
            OrderDto orderDto = new OrderDto();
            orderDto.setAddressId(2L);
            orderDto.setDeliveryId(1L);

            // when & then
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isUnauthorized());
        }

    @Test
    void getOrderById() {
    }

    @Test
    void deleteOrderById() {
    }

    @Test
    void getAllOrdersPaginated() {
    }

    @Test
    void processPayment() {
    }

    @Test
    void cancelOrderById() {
    }

    @Test
    void successOrderById() {
    }
}