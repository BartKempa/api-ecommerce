package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.order.OrderService;
import com.example.apiecommerce.domain.order.dto.OrderDto;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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


    @Operation(
            summary = "Create a new order based on cart",
            description = "Create a new order based on cart and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderFullDto.class),
                            examples = @ExampleObject(value = """
                                                    {
                                                    "id": 1,
                                                         "orderItems": [
                                                             {
                                                                 "id": 1,
                                                                 "orderItemQuantity": 1,
                                                                 "orderId": 1,
                                                                 "productId": 11,
                                                                 "productName": "Likier Baileys",
                                                                 "productPrice": 70.0
                                                             },
                                                             {
                                                                 "id": 2,
                                                                 "orderItemQuantity": 1,
                                                                 "orderId": 1,
                                                                 "productId": 1,
                                                                 "productName": "Pilsner",
                                                                 "productPrice": 8.8
                                                             }
                                                         ],
                                                         "orderTotalPrice": 78.8,
                                                         "streetName": "Suwalska",
                                                         "buildingNumber": "123",
                                                         "apartmentNumber": "321",
                                                         "zipCode": "80800",
                                                         "city": "Gdansk",
                                                         "userFirstName": "Bartek",
                                                         "userLastName": "Kempiak",
                                                         "userEmail": "bartek@mail.com",
                                                         "userPhoneNumber": "123456789"
                                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                    {
                        "message": "Invalid input",
                        "timestamp": "2025-01-21T14:45:00"
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                    {
                        "message": "Internal server error",
                        "timestamp": "2025-01-21T14:45:00"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    ResponseEntity<OrderFullDto> createOrderBasedOnCart(
            @Valid @RequestBody OrderDto orderDto,
            Authentication authentication){
        String username = authentication.getName();
        OrderFullDto orderFullDto = orderService.createOrder(username, orderDto.getAddressId());
        URI savedOrderUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderFullDto.getId())
                .toUri();
        return ResponseEntity.created(savedOrderUri).body(orderFullDto);
    }


}
