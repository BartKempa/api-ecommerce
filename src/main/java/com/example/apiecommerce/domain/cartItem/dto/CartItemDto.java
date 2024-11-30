package com.example.apiecommerce.domain.cartItem.dto;

public class CartItemDto {
    private Long id;
    private Long cartItemQuantity;
    private Long cartId;
    private Long productId;

    public CartItemDto(Long id, Long cartItemQuantity, Long cartId, Long productId) {
        this.id = id;
        this.cartItemQuantity = cartItemQuantity;
        this.cartId = cartId;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartItemQuantity() {
        return cartItemQuantity;
    }

    public void setCartItemQuantity(Long cartItemQuantity) {
        this.cartItemQuantity = cartItemQuantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
