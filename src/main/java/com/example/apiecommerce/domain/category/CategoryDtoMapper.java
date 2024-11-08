package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;

class CategoryDtoMapper {
    static CategoryDto map(Category category){
        return new CategoryDto(
                category.getId(),
                category.getCategoryName()
        );
    }

    static Category map(CategoryDto categoryDto){
        return new Category(
                categoryDto.getId(),
                categoryDto.getCategoryName()
        );
    }
}
