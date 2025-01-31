package com.example.apiecommerce.domain.creditCard.dto;

public class CreditCardForReturnDto {
    private Long id;
    private String abbreviationCardNumber;
    private String abbreviationCardValidity;
    private String abbreviationCardCVV;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbbreviationCardNumber() {
        return abbreviationCardNumber;
    }

    public void setAbbreviationCardNumber(String abbreviationCardNumber) {
        this.abbreviationCardNumber = abbreviationCardNumber;
    }

    public String getAbbreviationCardValidity() {
        return abbreviationCardValidity;
    }

    public void setAbbreviationCardValidity(String abbreviationCardValidity) {
        this.abbreviationCardValidity = abbreviationCardValidity;
    }

    public String getAbbreviationCardCVV() {
        return abbreviationCardCVV;
    }

    public void setAbbreviationCardCVV(String abbreviationCardCVV) {
        this.abbreviationCardCVV = abbreviationCardCVV;
    }
}
