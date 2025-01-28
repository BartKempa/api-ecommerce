package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.order.OrderService;
import com.example.apiecommerce.domain.order.dto.OrderDto;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;



    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    ResponseEntity<OrderFullDto> createOrderBasedOnCart(@RequestBody OrderDto orderDto, Authentication authentication){
        String username = authentication.getName();
        OrderFullDto orderFullDto = orderService.createOrder(username, orderDto.getAddressId());
        URI savedOrderUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(orderFullDto.getId())
                .toUri();
        return ResponseEntity.created(savedOrderUri).body(orderFullDto);
    }
}
