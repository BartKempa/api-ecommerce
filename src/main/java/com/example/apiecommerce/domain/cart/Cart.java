package com.example.apiecommerce.domain.cart;

import com.example.apiecommerce.domain.cartItem.CartItem;
import com.example.apiecommerce.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime creationDate;
    @OneToOne(mappedBy = "cart")
    private User user;
    @OneToMany(mappedBy = "cart")
    private Set<CartItem> cartItems = new HashSet<>();
}
