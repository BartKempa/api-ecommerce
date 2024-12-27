package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cartItem.CartItemService;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    ResponseEntity<CartItemDto> addCartItem(@RequestBody CartItemDto cartItemDto){
        CartItemDto savedCartItem = cartItemService.addCartItemToCart(cartItemDto);
        URI savedCartItemUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartItem.getId())
                .toUri();
        return ResponseEntity.created(savedCartItemUri).body(savedCartItem);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCartItemById(@PathVariable Long id){
        cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
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
    }
}
