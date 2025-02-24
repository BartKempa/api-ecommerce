package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.cartItem.CartItem;
import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.orderItem.OrderItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private double productPrice;
    private String description;
    private LocalDateTime creationDate;
    private long productQuantity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private Set<CartItem> cartItems = new HashSet<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private Set<OrderItem> orderItems = new HashSet<>();

    public Product() {
    }

    public Product(Long id, String productName, Double productPrice, String description, LocalDateTime creationDate, Long productQuantity, Category category) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.description = description;
        this.creationDate = creationDate;
        this.productQuantity = productQuantity;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", productQuantity=" + productQuantity +
                ", category=" + category +
                ", cartItems=" + cartItems +
                ", orderItems=" + orderItems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
