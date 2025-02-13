package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.CategoryService;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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


    @Operation(
            summary = "Create a new category",
            description = "Create a new category and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class),
                            examples = @ExampleObject(value = """
                                {
                                        "id": 1,
                                        "categoryName": "Piwo"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                    {
                        "message": "Invalid input",
                        "timestamp": "2025-01-21T14:45:00"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    ResponseEntity<CategoryDto> addCategory(
            @Valid @RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto = categoryService.addCategory(categoryDto);
        URI savedCategoryDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategoryDto.getId())
                .toUri();
        return ResponseEntity.created(savedCategoryDtoUri).body(savedCategoryDto);
    }


    @Operation(
            summary = "Get all categories",
            description = "Retrieve a list of all categories"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the list of all categories",
                    content =  @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)),
                            examples = @ExampleObject(value = """
                            [
                                {
                                    "id": 1,
                                    "categoryName": "Piwo"
                                },
                                {
                                    "id": 2,
                                    "categoryName": "Wino"
                                }                               
                            ]
                            """)
                    )
            )
    })
    @GetMapping
    List<CategoryDto> getAllCategories(){
        return categoryService.findAllCategories();
    }


    @Operation(
            summary = "Replace a category",
            description = "Update all details of category"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category updated successfully",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "categoryName": "Wino"
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                    {
                        "message": "Invalid input",
                        "timestamp": "2025-01-21T14:45:00"
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Category not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PutMapping("/{id}")
    ResponseEntity<?> replaceCategory(
            @RequestBody CategoryDto categoryDto,
            @Parameter(
                    description = "id of category to be updated",
                    required = true,
                    example = "1")
            @PathVariable Long id){
        return categoryService.replaceCategory(id, categoryDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Delete a category",
            description = "Delete a category by its id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Category successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Address not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCategory(
            @Parameter(
                    description = "id of category to be deleted",
                    required = true,
                    example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}


