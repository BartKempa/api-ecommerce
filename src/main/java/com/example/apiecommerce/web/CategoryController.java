package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.CategoryService;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Create a new category", description = "Create a new category and add it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class),
                    examples = @ExampleObject(value = """
                                {
                                        "id": 1,
                                        "categoryName": "Piwo"
                                }"""))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content) })
    @PostMapping
    ResponseEntity<CategoryDto> addCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category to created", required = true,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryDto.class),
            examples = @ExampleObject(value = """
                    {
                        "categoryName":"Piwo"
                    }""")))
            @RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto = categoryService.addCategory(categoryDto);
        URI savedCategoryDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategoryDto.getId())
                .toUri();
        return ResponseEntity.created(savedCategoryDtoUri).body(savedCategoryDto);
    }

    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Got the list of all categories",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)) )})
    @GetMapping
    List<CategoryDto> getAllCategories(){
        return categoryService.findAllCategories();
    }

    @Operation(summary = "Replace a category", description = "Update all details of category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Category updated successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "categoryName": "Wino"
                            }
                        """))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Category not found",
                    content = @Content) })
    @PutMapping("/{id}")
    ResponseEntity<?> replaceCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category to updated", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDto.class),
                    examples = @ExampleObject(value = """
                            {
                              "categoryName": "Wino"
                            }
                        """)
    ) )
                                      @RequestBody CategoryDto categoryDto,
                                      @Parameter(description = "id of category to be updated", required = true, example = "1")
                                      @PathVariable Long id){
        return categoryService.replaceCategory(id, categoryDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a category", description = "Delete a category by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Category deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Category not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCategory(@Parameter(description = "id of category to be deleted", required = true, example = "1")
                                     @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}


