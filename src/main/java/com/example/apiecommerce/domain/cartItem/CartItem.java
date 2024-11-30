package com.example.apiecommerce.domain.cartItem;

import com.example.apiecommerce.domain.cart.Cart;
import com.example.apiecommerce.domain.product.Product;
import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cartItemQuantity;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public CartItem(Long id, Long cartItemQuantity, Cart cart, Product product) {
        this.id = id;
        this.cartItemQuantity = cartItemQuantity;
        this.cart = cart;
        this.product = product;
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

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
