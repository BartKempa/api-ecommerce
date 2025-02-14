package com.example.apiecommerce.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object for Cart")
public class CartDto {

    @Schema(description = "Unique identifier of the cart", example = "1")
    private Long id;

    @Schema(description = "The date and time when the cart was created", example = "2025-02-13T10:15:30")
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
