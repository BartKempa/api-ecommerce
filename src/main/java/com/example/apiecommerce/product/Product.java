package com.example.apiecommerce.product;

import com.example.apiecommerce.cartItem.CartItem;
import com.example.apiecommerce.category.Category;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private Double productPrice;
    private String description;
    private LocalDateTime creationDate;
    private Long productQuantity;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @OneToMany(mappedBy = "product")
    private Set<CartItem> cartItems = new HashSet<>();
}
