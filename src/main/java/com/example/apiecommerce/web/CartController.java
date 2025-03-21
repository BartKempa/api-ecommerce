package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDetailsDto;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
            summary = "Get details about user cart",
            description = "Retrieve details about all cart items from user cart and total cost of this cart" )
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
    @GetMapping()
    ResponseEntity<?> getCartDetails(Authentication authentication){
        String username = authentication.getName();
        return cartService.findUserCart(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }


    @Operation(
            summary = "Delete a cart",
            description = "Delete a cart by user")
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
                                        "message": "User does not have a cart",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping()
    ResponseEntity<?> deleteUserCart(Authentication authentication){
        String username = authentication.getName();
        cartService.deleteCartWithIncreasingStock(username);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Empty a cart",
            description = "Empty a cart by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart is empty"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User does not have a cart",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value =
                                    """
                                    {
                                        "message": "User does not have a cart",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/clear")
    public ResponseEntity<?> emptyCart(Authentication authentication) {
        String username = authentication.getName();
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
