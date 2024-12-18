package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.CategoryService;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content) })
    @PostMapping
    ResponseEntity<CategoryDto> addCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category to created", required = true,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryDto.class),
            examples = @ExampleObject(value = "{\n" +
                    "    \"categoryName\":\"Piwo\"\n" +
                    "}")))
            @RequestBody CategoryDto categoryDto){
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


