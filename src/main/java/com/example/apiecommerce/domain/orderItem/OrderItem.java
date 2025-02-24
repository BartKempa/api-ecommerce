package com.example.apiecommerce.domain.orderItem;

import com.example.apiecommerce.domain.order.Order;
import com.example.apiecommerce.domain.product.Product;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long orderItemQuantity;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderItemQuantity() {
        return orderItemQuantity;
    }

    public void setOrderItemQuantity(Long orderItemQuantity) {
        this.orderItemQuantity = orderItemQuantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem orderItem)) return false;
        return id != null && id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
