package com.example.apiecommerce.domain.creditCard.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.CreditCardNumber;

public class CreditCardDto {

    private Long id;

    @NotNull
    @CreditCardNumber(message = "Invalid credit card number")
    private String cardNumber;

    @Pattern(regexp = "(0[1-9]|1[0-2])/(\\d{2}|\\d{4})", message = "Invalid format. Use MM/YY or MM/YYYY")
    private String cardValidity;

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

}
