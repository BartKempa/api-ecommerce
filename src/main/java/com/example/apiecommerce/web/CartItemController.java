package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.cartItem.CartItemService;
import com.example.apiecommerce.domain.cartItem.dto.CartItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cartItem")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    ResponseEntity<CartItemDto> addCartItem(@RequestBody CartItemDto cartItemDto){
        CartItemDto savedCartItem = cartItemService.saveCartItem(cartItemDto);
        URI savedCartItemUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCartItem.getId())
                .toUri();
        return ResponseEntity.created(savedCartItemUri).body(savedCartItem);
    }
}
