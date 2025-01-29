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

    public OrderFullDto map(Order order){
        if (order == null){
            return null;
        }
        OrderFullDto orderDto = new OrderFullDto();
        orderDto.setId(order.getId());
        orderDto.setOrderItems(order.getOrderItems().stream().map(orderItemDtoMapper::map).collect(Collectors.toSet()));
        orderDto.setOrderTotalPrice(order.getTotalPrice());
        orderDto.setStreetName(order.getAddress().getStreetName());
        orderDto.setBuildingNumber(order.getAddress().getBuildingNumber());
        orderDto.setApartmentNumber(order.getAddress().getApartmentNumber());
        orderDto.setZipCode(order.getAddress().getZipCode());
        orderDto.setCity(order.getAddress().getCity());
        orderDto.setUserFirstName(order.getUser().getFirstName());
        orderDto.setUserLastName(order.getUser().getLastName());
        orderDto.setUserEmail(order.getUser().getEmail());
        orderDto.setUserPhoneNumber(order.getUser().getPhoneNumber());
        return orderDto;
    }
}
