package com.example.apiecommerce.domain.cart.dto;

import java.time.LocalDateTime;

public class CartDto {
    private Long id;
    private LocalDateTime creationDate;


    public CartDto(Long id, LocalDateTime creationDate) {
        this.id = id;
        this.creationDate = creationDate;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


}
