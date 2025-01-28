package com.example.apiecommerce.domain.orderItem;

import com.example.apiecommerce.domain.orderItem.dto.OrderItemDto;
import org.springframework.stereotype.Service;

@Service
public class OrderItemDtoMapper {


    public OrderItemDto map(OrderItem orderItem){
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setOrderItemQuantity(orderItem.getOrderItemQuantity());
        orderItemDto.setOrderId(orderItem.getOrder().getId());
        orderItemDto.setProductId(orderItem.getProduct().getId());
        orderItemDto.setProductName(orderItem.getProduct().getProductName());
        orderItemDto.setProductPrice(orderItem.getProduct().getProductPrice());
        return orderItemDto;
    }

}
