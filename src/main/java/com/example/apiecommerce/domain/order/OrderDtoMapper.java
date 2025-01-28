package com.example.apiecommerce.domain.order;

import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.orderItem.OrderItemDtoMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderDtoMapper {
    private final OrderItemDtoMapper orderItemDtoMapper;

    public OrderDtoMapper(OrderItemDtoMapper orderItemDtoMapper) {
        this.orderItemDtoMapper = orderItemDtoMapper;
    }

    OrderFullDto map(Order order){
        if (order == null){
            return null;
        }
        OrderFullDto orderDto = new OrderFullDto();
        orderDto.setOrderItems(order.getOrderItems().stream().map(orderItemDtoMapper::map).collect(Collectors.toSet()));
        orderDto.setOrderTotalPrice(order.getTotalPrice());
        orderDto.setAddress(orderDto.getAddress());
        orderDto.setUserFirstName(orderDto.getUserFirstName());
        orderDto.setUserLastName(orderDto.getUserFirstName());
        orderDto.setUserEmail(orderDto.getUserEmail());
        orderDto.setUserPhoneNumber(orderDto.getUserPhoneNumber());
        return orderDto;
    }
}
