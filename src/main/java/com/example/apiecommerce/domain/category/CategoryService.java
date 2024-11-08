package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto){
        Category category = CategoryDtoMapper.map(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryDtoMapper.map(savedCategory);
    }
}
