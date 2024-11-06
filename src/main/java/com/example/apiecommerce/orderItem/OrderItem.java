package com.example.apiecommerce.orderItem;

import com.example.apiecommerce.cart.Cart;
import com.example.apiecommerce.order.Order;
import com.example.apiecommerce.product.Product;
import jakarta.persistence.*;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderItemQuantity;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
