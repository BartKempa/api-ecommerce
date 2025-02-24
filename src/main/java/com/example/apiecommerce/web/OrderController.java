package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.order.OrderService;
import com.example.apiecommerce.domain.order.dto.OrderDto;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.order.dto.OrderMainInfoDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
        OrderFullDto orderFullDto = orderService.createOrder(username, orderDto.getAddressId(), orderDto.getDeliveryId());
        URI savedOrderUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderFullDto.getId())
                .toUri();
        return ResponseEntity.created(savedOrderUri).body(orderFullDto);
    }


    @Operation(
            summary = "Get an order by its id",
            description = "Retrieve an order by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the order",
                    content =  @Content(
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
                                                             }
                                                         ],
                                                         "orderTotalPrice": 70.0,
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
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Order not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<OrderFullDto> getOrderById(
            @Parameter(
                    description = "id of order to be searched",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id,
            Authentication authentication){
        String userName = authentication.getName();
        return orderService.findOrderById(id, userName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Delete an order",
            description = "Delete an order by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value =
                                    """
                                    {
                                        "message": "Order not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteOrderById(
            @Parameter(
                    description = "id of order to be deleted",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long id){
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get all orders with pagination",
            description = "Retrieve a paginated list of all orders"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of paginated orders",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object", implementation = Page.class),
                            examples = @ExampleObject(value = """
                                {
                                    "content": [
                                        {
                                            "id": 1,
                                            "orderDate": "2025-01-30T12:06:39.480092",
                                            "orderTotalPrice": 8.8,
                                            "userEmail": "bartek@mail.com",
                                            "userPhoneNumber": "123456789"
                                        }
                                    ],
                                    "pageable": {
                                        "pageNumber": 0,
                                        "pageSize": 6
                                    },
                                    "totalElements": 1,
                                    "totalPages": 1
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page number or sort field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/page")
    Page<OrderMainInfoDto> getAllOrdersPaginated(
            @Parameter(
                    description = "Page number (default: 1)",
                    required = false)
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @Parameter(
                    description = "Page size - number of orders per page (default: 6)",
                    required = false)
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize,
            @Parameter(
                    description = "Sort field - the field that determines the order in which orders appears on (default: 'orderDate')",
                    required = false)
            @RequestParam(value = "sortField", defaultValue = "orderDate") String sortField,
            @Parameter(
                    description = "Sort direction - the field that determines the direction in which orders appears on (default: ascending)",
                    required = false)
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection){
        return orderService.findAllPaginatedOrders(pageNo, pageSize, sortField, sortDirection);
    }


    @Operation(
            summary = "Process a payment order by its id",
            description = "Process a payment order by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "payment order was processed successfully",
                    content =  @Content(
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
                                                             }
                                                         ],
                                                         "orderTotalPrice": 70.0,
                                                         "orderPaymentStatus": "COMPLETED",
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
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Order not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/{orderId}/payments")
    ResponseEntity<?> processPayment(
            @Parameter(
                    description = "id of order for which payment will be processed",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long orderId){
        return orderService.processPayment(orderId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }


    @Operation(
            summary = "Cancel an order",
            description = "Cancel an order by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order Canceled"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value =
                                    """
                                    {
                                        "message": "Order not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/cancel")
    ResponseEntity<?> cancelOrderById(
            @Parameter(
                    description = "id of order to be canceled",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long id){
        orderService.cancelOrderById(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Mark order as SUCCESS",
            description = "Mark order as SUCCESS by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order succeed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value =
                                    """
                                    {
                                        "message": "Order not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/success")
    ResponseEntity<?> successOrderById(
            @Parameter(
                    description = "id of the order to mark as SUCCESS",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long id){
        orderService.successOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
