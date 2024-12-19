package com.example.apiecommerce.domain.product.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProductDto {
    private Long id;
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 2, max = 100)
    private String productName;
    @NotNull
    @Positive
    private Double productPrice;
    @NotBlank(message = "Product description cannot be empty")
    @Size(min = 2, max = 2000)
    private String description;
    private LocalDateTime creationDate;
    @NotNull
    @PositiveOrZero
    private Long productQuantity;
    @NotNull
    private Long categoryId;
    private String categoryName;

    public ProductDto() {
    }

    public ProductDto(Long id, String productName, Double productPrice, String description, LocalDateTime creationDate, Long productQuantity, Long categoryId, String categoryName) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.description = description;
        this.creationDate = creationDate;
        this.productQuantity = productQuantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(productName, that.productName) && Objects.equals(productPrice, that.productPrice) && Objects.equals(description, that.description) && Objects.equals(creationDate, that.creationDate) && Objects.equals(productQuantity, that.productQuantity) && Objects.equals(categoryId, that.categoryId) && Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, productPrice, description, creationDate, productQuantity, categoryId, categoryName);
    }
}
