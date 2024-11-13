package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;

class CategoryDtoMapper {
    static CategoryDto map(Category category){
        if (category == null)
            return null;
        return new CategoryDto(
                category.getId(),
                category.getCategoryName()
        );
    }

    static Category map(CategoryDto categoryDto){
        if (categoryDto == null)
            return null;
        return new Category(
                categoryDto.getId(),
                categoryDto.getCategoryName()
        );
    }
}
