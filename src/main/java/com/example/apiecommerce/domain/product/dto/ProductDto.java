package com.example.apiecommerce.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "Data Transfer Object for Product")
public class ProductDto {
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "Pilsner")
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 2, max = 100)
    private String productName;

    @Schema(description = "Price of the product", example = "10.99")
    @NotNull
    @Positive
    private Double productPrice;

    @Schema(description = "Product description", example = "Piwo górnej fermentacji, charakteryzujące się mocnym chmielowym smakiem i wyrazistą goryczką.")
    @NotBlank(message = "Product description cannot be empty")
    @Size(min = 2, max = 2000)
    private String description;

    @Schema(description = "Date of product creation", example = "2024-02-12T12:30:00")
    private LocalDateTime creationDate;

    @Schema(description = "Available quantity of the product", example = "50")
    @NotNull
    @PositiveOrZero
    private Long productQuantity;

    @Schema(description = "Category ID to which the product belongs", example = "3")
    @NotNull
    private Long categoryId;

    @Schema(description = "Category name", example = "Piwo")
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
