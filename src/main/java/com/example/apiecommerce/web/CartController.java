package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(
            summary = "Create a new cart",
            description = "Create a new cart and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cart created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "creationDate": "2025-01-21T14:45:00",
                      "userId": 1
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    ResponseEntity<CartDto> createCart(Authentication authentication){
        String username = authentication.getName();
        CartDto savedCartDto = cartService.createCart(username);
        URI savedCartDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartDto.getId())
                .toUri();
        return ResponseEntity.created(savedCartDtoUri).body(savedCartDto);
    }


    @Operation(
            summary = "Get details about cart by its id",
            description = "Retrieve details about all cart items form cart by its id and total cost of this cart" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the cart",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDetailsDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "cartItems": [
                                            {
                                                "id": 1,
                                                "cartItemQuantity": 10,
                                                "cartId": 1,
                                                "productId": 3,
                                                "productName": "Porter",
                                                "productPrice": 12.0
                                            }
                                        ],
                                        "totalCost": 120.0
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                            )
                    )
    })
    @GetMapping("/{id}")
    ResponseEntity<?> getCartDetails(
            @Parameter(
                    description = "Id of cart to be searched.",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id){
        return cartService.findCartDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }


    @Operation(
            summary = "Delete a cart",
            description = "Delete a cart by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value =
                                    """
                                    {
                                        "message": "Cart not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCart(
            @Parameter(
                    description = "id of cart to be deleted",
                    required = true,
                    example = "1")
            @PathVariable Long id){
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
