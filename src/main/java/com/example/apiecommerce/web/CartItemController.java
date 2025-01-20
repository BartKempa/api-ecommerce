package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cartItem.CartItemService;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import com.example.apiecommerce.domain.cartItem.dto.CartItemFullDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content) })
    @PostMapping
    ResponseEntity<CartItemFullDto> addCartItemToCart(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CartItem to create", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartItemDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "productId": 1
                                    }
                                    """)
                    )
            )
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
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCartItemById(
            @Parameter(description = "ID of the cart item to be deleted", required = true, example = "1")
            @PathVariable Long id){
        cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

/*    @GetMapping("/{id}")
    ResponseEntity<CartItemDto> getCartItemById(@PathVariable Long id){
        return cartItemService.findCartItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceCartItem(@PathVariable Long id, @RequestBody CartItemDto cartItemDto){
        return cartItemService.replaceCartItem(id, cartItemDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }*/
}
