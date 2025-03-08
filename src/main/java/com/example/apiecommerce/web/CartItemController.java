package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cartItem.CartItemService;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemUpdateQuantityDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cartItems")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }


    @Operation(
            summary = "Create a new cartItem",
            description = "Create a new cartItem and add it to the cart"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "CartItem created successfully",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartItemFullDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "cartItemQuantity": 1,
                                        "cartId": 1,
                                        "productId": 1
                                    }
                                    """
                            )
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
    ResponseEntity<CartItemFullDto> addCartItemToCart(
            @Valid @RequestBody CartItemDto cartItemDto,
            Authentication authentication){
        String username = authentication.getName();
        CartItemFullDto savedCartItemFullDto = cartItemService.addCartItemToCart(username, cartItemDto);
        URI savedCartItemUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartItemFullDto.getId())
                .toUri();
        return ResponseEntity.created(savedCartItemUri).body(savedCartItemFullDto);
    }


    @Operation(
            summary = "Delete an cart item",
            description = "Delete an cart item by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart item successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart item not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCartItemById(
            @Parameter(description = "ID of the cart item to be deleted", required = true, example = "1")
            @PathVariable Long id,
            Authentication authentication){
        String username = authentication.getName();
        cartItemService.deleteCartItem(id, username);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Update cart item quantity",
            description = "Partially update cart item details by its ID. Only quantity field will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart item quantity updated successfully(no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart item not found",
                                        "timestamp": "2025-01-21T14:45:00"
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
            )
    })
    @PatchMapping("/{id}")
    ResponseEntity<?> updateCartItemQuantity(
            @Parameter(
                    description = "ID of the cart item to be updated",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody CartItemUpdateQuantityDto cartItemUpdateQuantityDto){
        String username = authentication.getName();
        cartItemService.updateCartItemQuantity(id, cartItemUpdateQuantityDto, username);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Increase cart item quantity by 1",
            description = "Partially update cart item details by its ID. Only quantity field will be increase by 1."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart item quantity increased by 1 successfully(no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart item not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/quantity/increment")
    ResponseEntity<?> increaseCartItemQuantityByOne(
            @Parameter(
                    description = "ID of the cart item to be increased",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            Authentication authentication){
        String username = authentication.getName();
        cartItemService.increaseCartItemQuantityByOne(id, username);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Reduce cart item quantity by 1",
            description = "Partially update cart item details by its ID. Only quantity field will be reduced by 1."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cart item quantity reduced by 1 successfully(no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart item not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/quantity/decrement")
    ResponseEntity<?> reduceCartItemQuantityByOne(
            @Parameter(
                    description = "ID of the cart item to be reduced",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            Authentication authentication){
        String username = authentication.getName();
        cartItemService.reduceCartItemQuantityByOne(id, username);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get a cart item by its id",
            description = "Retrieve a cart item by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the cart item",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartItemFullDto.class),
                            examples = @ExampleObject(value = """
                                        {
                                          "id": 1,
                                          "cartItemQuantity": 2,
                                          "cartId": 1,
                                          "productId": 1,
                                          "productName" : "Pilsner",
                                          "productPrice" : 8.8
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Cart item not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
   @GetMapping("/{id}")
   ResponseEntity<CartItemFullDto> getCartItemById(
            @Parameter(
                    description = "Id of cart item to be searched.",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id,
            Authentication authentication){
        String username = authentication.getName();
        return cartItemService.findCartItemById(id, username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
