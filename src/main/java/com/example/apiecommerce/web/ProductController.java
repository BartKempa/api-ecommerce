package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.product.ProductService;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Create a new product and add it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Product created successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "productName": "Pilsner urquell",
                              "productPrice": 8.60,
                              "description": "Klasyczne czeskie piwo",
                              "creationDate": "2024-12-18T12:00:00",
                              "productQuantity": 20,
                              "categoryId": 1,
                              "categoryName": "Piwo"
                            }"""))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content) })
    @PostMapping
    ResponseEntity<ProductDto> addProduct(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product to create", required = true,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductDto.class),
            examples = @ExampleObject(value = """
                     {
                        "productName": "Pilsner urquell",
                        "productPrice": 8.60,
                        "description": "Klasyczne czeskie piwo",
                        "productQuantity": 20,
                        "categoryId": 1
                    }""")))
            @Valid @RequestBody ProductDto productDto){
        ProductDto savedProduct = productService.saveProduct(productDto);
        URI savedProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
        return ResponseEntity.created(savedProductUri).body(savedProduct);
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Got the list of all products",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class)) )})
    @GetMapping
    List<ProductDto> getAllProducts(){
        return productService.findAllProducts();
    }

    @Operation(summary = "Get all products with pagination", description = "Retrieve a paginated list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Got the list of all products",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class)) )})
    @GetMapping("/page/{pageNo}")
    Page<ProductDto> getAllProductsPaginated(@Parameter(description = "Page number (default: 1)", required = false)
                                             @PathVariable Optional<Integer> pageNo,
                                             @Parameter(description = "Page size - number of products per page (default: 6)", required = false)
                                             @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                                             @Parameter(description = "Sort field - the field that determines the order in which products appears on (default: 'productName')", required = false)
                                             @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
                                             @Parameter(description = "Sort direction - the field that determines the direction in which products appears on (default: ascending)", required = false)
                                             @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection){
        int pageNumber = pageNo.orElse(1);
        return productService.findAllPaginatedProducts(pageNumber, pageSize, sortField, sortDirection);
    }

    @Operation(summary = "Get all products paginated from chosen category", description = "Retrieve a paginated list of all products from chosen category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Got the paginated list of all products from chosen category",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class)) )})
    @GetMapping("/page/{pageNo}/category")
    Page<ProductDto> getAllProductsFromCategoryPaginated(@Parameter(description = "Page number", required = false)
                                                         @PathVariable(required = false) Optional<Integer> pageNo,
                                                         @Parameter(description = "Page size - number of products per page", required = false)
                                                         @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                                         @Parameter(description = "Sort field - the field that determines the order in which products appears on", required = false)
                                                         @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
                                                         @Parameter(description = "Sort direction - the field that determines the direction in which products appears on", required = false)
                                                         @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                         @Parameter(description = "Category name - the field that determines the category from which products appears on", required = true, example = "Piwo")
                                                         @RequestParam(value = "categoryName") String categoryName){
        int pageNumber = pageNo.orElse(1);
        return productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, categoryName);
    }

    @Operation(summary = "Get a product by its id", description = "Retrieve a product by its id" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the product",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "productName": "Pilsner urquell",
                              "productPrice": 8.60,
                              "description": "Klasyczne czeskie piwo",
                              "creationDate": "2024-12-18T12:00:00",
                              "productQuantity": 20,
                              "categoryId": 1,
                              "categoryName": "Piwo"
                            }
                        """))),
            @ApiResponse(responseCode = "404",
                    description = "product not found",
                    content = @Content) })
    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProductById(@Parameter(description = "id of product to be searched", required = true, example = "1")
                                              @PathVariable Long id){
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a product", description = "Delete a product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Product deleted"
            ),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProductById(@Parameter(description = "id of product to be deleted", required = true, example = "1")
                                        @PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Replace a product", description = "Update all details of product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Product updated successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "productName": "Pilsner urquell",
                              "productPrice": 8.60,
                              "description": "Klasyczne czeskie piwo",
                              "creationDate": "2024-12-18T12:00:00",
                              "productQuantity": 20,
                              "categoryId": 1,
                              "categoryName": "Piwo"
                            }
                        """))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content) })
    @PutMapping("/{id}")
    ResponseEntity<?> replaceProduct(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product to updated", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "productName": "Zlaty bazant",
                                "productPrice": 6.60,
                                "description": "Klasyczne słowackie piwo",
                                "productQuantity": 18,
                                "categoryId": 1
                            }""")))
                                     @Valid @RequestBody ProductDto productDto,
                                     @Parameter(description = "id of product to be updated", required = true, example = "1")
                                     @PathVariable Long id
                                     ){
        return productService.replaceProduct(id, productDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Quantity of the product", description = "Get the quantity of the product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Quantity of the product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "integer", example = "10"))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content) })
    @GetMapping("{id}/quantity")
    ResponseEntity<?> getProductQuantity(@Parameter(description = "id of the product to retrieve the quantity", required = true, example = "1")
                                         @PathVariable Long id){
        return productService.countQuantityOfProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
