package com.example.apiecommerce.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for Category")
public class CategoryDto {

    @Schema(description = "Unique identifier for the category", example = "1")
    private Long id;

    @Schema(description = "The name of the category", example = "Piwo", required = true, minLength = 2, maxLength = 100)
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
