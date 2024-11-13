package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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

    public List<CategoryDto> findAllCategories(){
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(CategoryDtoMapper::map)
                .toList();
    }

    @Transactional
    public Optional<CategoryDto> replaceCategory(Long categoryId, CategoryDto categoryDto){
        if (!categoryRepository.existsById(categoryId)){
            return Optional.empty();
        }
        categoryDto.setId(categoryId);
        Category categoryToUpdate = CategoryDtoMapper.map(categoryDto);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return Optional.of(CategoryDtoMapper.map(updatedCategory));
    }

    @Transactional
    public void deleteCategory(Long categoryId){
        if (!categoryRepository.existsById(categoryId)){
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(categoryId);
    }
}
