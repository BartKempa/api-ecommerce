package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.CategoryService;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/categories")
    ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto = categoryService.addCategory(categoryDto);
        URI savedCategoryDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategoryDto.getId())
                .toUri();
        return ResponseEntity.created(savedCategoryDtoUri).body(savedCategoryDto);
    }
}
