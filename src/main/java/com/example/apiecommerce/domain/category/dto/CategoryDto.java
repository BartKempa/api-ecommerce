package com.example.apiecommerce.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDto {
    private Long id;
    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 2, max = 100)
    private String categoryName;

    public CategoryDto(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public CategoryDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
