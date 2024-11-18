package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.CategoryService;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto = categoryService.addCategory(categoryDto);
        URI savedCategoryDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategoryDto.getId())
                .toUri();
        return ResponseEntity.created(savedCategoryDtoUri).body(savedCategoryDto);
    }

    @GetMapping
    List<CategoryDto> getCategories(){
        return categoryService.findAllCategories();
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        return categoryService.replaceCategory(id, categoryDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}


