package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "creationDate": "2024-12-18T12:00:00",
                                      "userId": 1
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
    ResponseEntity<CartDto> createCart(Authentication authentication){
        String username = authentication.getName();
        CartDto savedCartDto = cartService.createCart(username);
        URI savedCartDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartDto.getId())
                .toUri();
        return ResponseEntity.created(savedCartDtoUri).body(savedCartDto);
    }


  /*  @Operation(
            summary = "Create a new cart",
            description = "Create a new cart and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cart created successfully",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "creationDate": "2024-12-18T12:00:00",
                                      "userId": 1
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
    ResponseEntity<CartDto> createCart(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of cart to save.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "userId": 1
                            }
                            """)
                    )
            )
            @Valid @RequestBody CartDto cartDto, Authentication authentication){
        String username = authentication.getName();
        CartDto savedCartDto = cartService.createCart(cartDto, username);
        URI savedCartDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartDto.getId())
                .toUri();
        return ResponseEntity.created(savedCartDtoUri).body(savedCartDto);
    }*/


    @GetMapping("/{id}")
    ResponseEntity<CartDto> getCart(@PathVariable Long id){
        return cartService.getCartById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCart(@PathVariable Long id){
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

}
