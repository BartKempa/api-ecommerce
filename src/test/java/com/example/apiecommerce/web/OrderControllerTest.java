package com.example.apiecommerce.web;


import com.example.apiecommerce.domain.order.Order;
import com.example.apiecommerce.domain.order.OrderRepository;
import com.example.apiecommerce.domain.order.OrderStatus;
import com.example.apiecommerce.domain.order.dto.OrderDto;
import com.example.apiecommerce.domain.orderItem.OrderItem;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private OrderRepository orderRepository;


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
                .andExpect(jsonPath("$.userFirstName").value("Janek"));
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
        void shouldFailed_whenUserIsNotAuthenticated() throws Exception {
            //given
            OrderDto orderDto = new OrderDto();
            orderDto.setAddressId(2L);
            orderDto.setDeliveryId(1L);

            //when & then
            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderDto)))
                    .andExpect(status().isUnauthorized());
        }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUserGetOrderById() throws Exception {
        //given
        long orderId = 2L;

        //when & then
        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.orderTotalPrice").value(120));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWhenUserTryGetOtherUsersOrderById() throws Exception {
        //given
        long orderId = 1L;

        //when & then
        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This order belongs to another user"));
    }

    @Test
    void shouldFailed_whenUserGetOrderByIdAndIsNotAuthenticated() throws Exception {
        //given
        long orderId = 1L;

        //when & then
        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "eighthUser@mail.com", roles = "USER")
    void shouldFailedWhenUserTryGetNotExistsOrder() throws Exception {
        //given
        long orderId = 999L;

        //when & then
        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminDeleteOrderById() throws Exception {
        //given
        long orderId = 2L;
        assertTrue(orderRepository.existsById(orderId));

        //when
        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(orderRepository.existsById(orderId));
    }

    @Test
    void shouldFailed_whenUserDeleteOrderByIdAndIsNotAuthenticated() throws Exception {
        //given
        long orderId = 2L;

        //when & then
        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailed_whenUserDeleteOrderByIdAndIsNotAuthorized() throws Exception {
        //given
        long orderId = 2L;

        //when & then
        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailedWhenAdminTryDeleteNotExistsOrder() throws Exception {
        //given
        long orderId = 999L;

        //when & then
        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminGetAllOrdersPaginatedAscending() throws Exception {
        //given
        long pageNo = 1L;

        //when
        mockMvc.perform(get("/api/v1/orders/page", pageNo)
                        .param("page", "1")
                .param("pageSize", "2")
                .param("sortField", "orderDate")
                .param("sortDirection", "ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].orderTotalPrice").value(120.0))
                .andExpect(jsonPath("$.content[1].orderTotalPrice").value(59.3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminGetAllOrdersPaginatedDescending() throws Exception {
        //given
        long pageNo = 1L;

        //when
        mockMvc.perform(get("/api/v1/orders/page", pageNo)
                        .param("page", "1")
                        .param("pageSize", "2")
                        .param("sortField", "orderDate")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5))
                .andExpect(jsonPath("$.content[0].orderTotalPrice").value(45.75))
                .andExpect(jsonPath("$.content[1].orderTotalPrice").value(200.0))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailed_whenUserGetAllOrdersPaginatedAndIsNotAuthorized() throws Exception {
        //given
        long pageNo = 1L;

        //when
        mockMvc.perform(get("/api/v1/orders/page", pageNo)
                        .param("page", "1")
                        .param("pageSize", "2")
                        .param("sortField", "orderDate")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailed_whenUserGetAllOrdersPaginatedAndIsNotAuthenticated() throws Exception {
        //given
        long pageNo = 1L;

        //when
        mockMvc.perform(get("/api/v1/orders/page", pageNo)
                        .param("page", "1")
                        .param("pageSize", "2")
                        .param("sortField", "orderDate")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUserProcessPayment() throws Exception {
        //given
        long orderId = 2L;

        //when
        mockMvc.perform(post("/api/v1/orders/{orderId}/payments", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailed_whenUserProcessPaymentAndIsNotAuthenticated() throws Exception {
        //given
        long orderId = 2L;

        //when
        mockMvc.perform(post("/api/v1/orders/{orderId}/payments", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminCancelOrderById() throws Exception {
        //given
        long orderId = 1L;
        assertTrue(orderRepository.existsById(orderId));
        assertTrue(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertTrue(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.CANCELLED));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailedWhenAdminCancelOrderByIdWithoutStatusNew() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only status 'NEW' can be changed into 'CANCELLED'"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailed_whenUserCancelOrderByIdAndIsNotAuthorized() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailed_whenUserCancelOrderByIAndIsNotAuthenticated() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenCancelingNonExistingOrder() throws Exception {
        //given
        long orderId = 999L;
        assertFalse(orderRepository.existsById(orderId));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailedWhenAdminSuccessOrderByIdWithoutStatusNew() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/success", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only orders with payment status 'COMPLETED' can be marked as 'SUCCESS'"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailed_whenUserSuccessOrderByIdAndIsNotAuthorized() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/success", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailed_whenUserSuccessOrderByIAndIsNotAuthenticated() throws Exception {
        //given
        long orderId = 3L;
        assertTrue(orderRepository.existsById(orderId));
        assertFalse(orderRepository.findById(orderId).get().getOrderStatus().equals(OrderStatus.NEW));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/success", orderId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenTrySetUpStatusSuccessAndOrderNotExist() throws Exception {
        //given
        long orderId = 999L;
        assertFalse(orderRepository.existsById(orderId));

        //when
        mockMvc.perform(patch("/api/v1/orders/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }
}