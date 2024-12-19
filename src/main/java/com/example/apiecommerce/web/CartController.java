package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cart.CartService;
import com.example.apiecommerce.domain.cart.dto.CartDto;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    ResponseEntity<CartDto> addCart(@RequestBody CartDto cartDto){
        CartDto savedCartDto = cartService.addCart(cartDto);
        URI savedCartDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartDto.getId())
                .toUri();
        return ResponseEntity.created(savedCartDtoUri).body(savedCartDto);
    }

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
