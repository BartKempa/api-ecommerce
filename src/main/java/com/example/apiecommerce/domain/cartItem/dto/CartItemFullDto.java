package com.example.apiecommerce.domain.cartItem.dto;

public class CartItemFullDto {
    private Long id;
    private Long cartItemQuantity;
    private Long cartId;
    private Long productId;
    private String productName;
    private double productPrice;

    public CartItemFullDto(Long id, Long cartItemQuantity, Long cartId, Long productId, String productName, double productPrice) {
        this.id = id;
        this.cartItemQuantity = cartItemQuantity;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
}
