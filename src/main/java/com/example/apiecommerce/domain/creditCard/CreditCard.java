package com.example.apiecommerce.domain.creditCard;

import com.example.apiecommerce.domain.user.User;
import jakarta.persistence.*;

@Entity
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNumber;
    private String cardValidity;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardValidity() {
        return cardValidity;
    }

    public void setCardValidity(String cardValidity) {
        this.cardValidity = cardValidity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
