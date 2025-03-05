package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.product.ProductService;
import com.example.apiecommerce.domain.product.dto.ProductDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDto> pagedResourcesAssembler;

    public ProductController(ProductService productService, PagedResourcesAssembler<ProductDto> pagedResourcesAssembler) {
        this.productService = productService;

        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }


    @Operation(
            summary = "Create a new product",
            description = "Create a new product and add it to the database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "id": 21,
                                    "productName": "Pilsner",
                                    "productPrice": 8.6,
                                    "description": "Klasyczne czeskie piwo",
                                    "creationDate": "2025-03-04T10:21:51.2223014",
                                    "productQuantity": 20,
                                    "categoryId": 1,
                                    "categoryName": "Piwo",
                                    "_links": {
                                        "self": [
                                            {
                                                "href": "http://localhost:8080/api/v1/products/21"
                                            },
                                            {
                                                "href": "http://localhost:8080/api/v1/products"
                                            }
                                        ]
                                    }
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
    ResponseEntity<EntityModel<ProductDto>> addProduct(
            @Valid @RequestBody ProductDto productDto){
        ProductDto savedProduct = productService.saveProduct(productDto);
        URI savedProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
        EntityModel<ProductDto> productDtoEntityModel = EntityModel.of(savedProduct);
        productDtoEntityModel.add(linkTo(methodOn(ProductController.class).getProductById(savedProduct.getId())).withSelfRel());
        productDtoEntityModel.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
        return ResponseEntity.created(savedProductUri).body(productDtoEntityModel);
    }

    @Operation(
            summary = "Get all products",
            description = "Retrieve a list of all products")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the list of all products",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CollectionModel.class),
                            examples = @ExampleObject(value = """
                                    {
                                              {
                                                    "id": 20,
                                                    "productName": "Szampan Veuve Clicquot",
                                                    "productPrice": 240.0,
                                                    "description": "Elegancki szampan o owocowym aromacie.",
                                                    "creationDate": "2024-02-18T14:00:00.478614",
                                                    "productQuantity": 10,
                                                    "categoryId": 7,
                                                    "categoryName": "Szampan",
                                                    "_links": {
                                                        "self": {
                                                            "href": "http://localhost:8080/api/v1/products/20"
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        "_links": {
                                            "self": {
                                                "href": "http://localhost:8080/api/v1/products"
                                            }
                                        }
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    ResponseEntity<CollectionModel<EntityModel<ProductDto>>> getAllProducts(){
        List<EntityModel<ProductDto>> entityModels = productService.findAllProducts()
                .stream().map(
                        productDto -> {
                            EntityModel<ProductDto> productDtoEntityModel = EntityModel.of(productDto);
                            productDtoEntityModel.add(linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel());
                            return productDtoEntityModel;
                        }
                ).toList();

        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(entityModels,
                linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel()
        );
        return ResponseEntity.ok(collectionModel);
    }


    @Operation(
            summary = "Get all products with pagination",
            description = "Retrieve a paginated list of all products"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the list of all products",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "_embedded": {
                                            "productDtoList": [
                                                {
                                                    "id": 4,
                                                    "productName": "Cabernet Sauvignon",
                                                    "productPrice": 45.0,
                                                    "description": "Czerwone wino o pełnym smaku z nutami owoców leśnych i przypraw.",
                                                    "creationDate": "2024-01-22T09:20:00.478614",
                                                    "productQuantity": 15,
                                                    "categoryId": 2,
                                                    "categoryName": "Wino",
                                                    "_links": {
                                                        "self": {
                                                            "href": "http://localhost:8080/api/v1/products/4"
                                                        }
                                                    }
                                                },
                                                {
                                                    "id": 6,
                                                    "productName": "Chardonnay",
                                                    "productPrice": 38.5,
                                                    "description": "Białe wino o delikatnym smaku z nutami owoców cytrusowych.",
                                                    "creationDate": "2024-02-01T12:00:00.478614",
                                                    "productQuantity": 25,
                                                    "categoryId": 2,
                                                    "categoryName": "Wino",
                                                    "_links": {
                                                        "self": {
                                                            "href": "http://localhost:8080/api/v1/products/6"
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        "_links": {
                                            "first": {
                                                "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC&page=0&size=2&sort=productName,asc"
                                            },
                                            "prev": {
                                                "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC&page=0&size=2&sort=productName,asc"
                                            },
                                            "self": [
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC&page=1&size=2&sort=productName,asc"
                                                },
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC"
                                                }
                                            ],
                                            "next": {
                                                "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC&page=2&size=2&sort=productName,asc"
                                            },
                                            "last": {
                                                "href": "http://localhost:8080/api/v1/products/page/2?pageSize=2&sortField=productName&sortDirection=ASC&page=9&size=2&sort=productName,asc"
                                            }
                                        },
                                        "page": {
                                            "size": 2,
                                            "totalElements": 20,
                                            "totalPages": 10,
                                            "number": 1
                                        }
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/page/{pageNo}")
    ResponseEntity<PagedModel<EntityModel<ProductDto>>> getAllProductsPaginated(
            @Parameter(
                    description = "Page number (default: 1)",
                    required = false)
            @PathVariable Optional<Integer> pageNo,
            @Parameter(
                    description = "Page size - number of products per page (default: 6)",
                    required = false)
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @Parameter(
                    description = "Sort field - the field that determines the order in which products appears on (default: 'productName')",
                    required = false)
            @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
            @Parameter(
                    description = "Sort direction - the field that determines the direction in which products appears on (default: ascending)",
                    required = false)
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection){
        int pageNumber = pageNo.orElse(1);

        Page<ProductDto> paginatedProducts = productService.findAllPaginatedProducts(pageNumber, pageSize, sortField, sortDirection);

        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(
                paginatedProducts,
                productDto -> EntityModel.of(productDto,
                        linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel()
                )
        );
        pagedModel.add(linkTo(methodOn(ProductController.class)
                .getAllProductsPaginated(pageNo, pageSize, sortField, sortDirection))
                .withSelfRel());
        return ResponseEntity.ok(pagedModel);
    }


    @Operation(
            summary = "Get all products paginated from chosen category",
            description = "Retrieve a paginated list of all products from chosen category")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the paginated list of all products from chosen category",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "_embedded": {
                                            "productDtoList": [
                                                {
                                                    "id": 4,
                                                    "productName": "Cabernet Sauvignon",
                                                    "productPrice": 45.0,
                                                    "description": "Czerwone wino o pełnym smaku z nutami owoców leśnych i przypraw.",
                                                    "creationDate": "2024-01-22T09:20:00.478614",
                                                    "productQuantity": 15,
                                                    "categoryId": 2,
                                                    "categoryName": "Wino",
                                                    "_links": {
                                                        "self": {
                                                            "href": "http://localhost:8080/api/v1/products/4"
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        "_links": {
                                            "first": {
                                                "href": "http://localhost:8080/api/v1/products/page/1/category?categoryName=Wino&pageSize=1&page=0&size=1&sort=productName,asc"
                                            },
                                            "self": [
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/page/1/category?categoryName=Wino&pageSize=1&page=0&size=1&sort=productName,asc"
                                                },
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/page/1/category?pageSize=1&sortField=productName&sortDirection=ASC&categoryName=Wino"
                                                }
                                            ],
                                            "next": {
                                                "href": "http://localhost:8080/api/v1/products/page/1/category?categoryName=Wino&pageSize=1&page=1&size=1&sort=productName,asc"
                                            },
                                            "last": {
                                                "href": "http://localhost:8080/api/v1/products/page/1/category?categoryName=Wino&pageSize=1&page=2&size=1&sort=productName,asc"
                                            }
                                        },
                                        "page": {
                                            "size": 1,
                                            "totalElements": 3,
                                            "totalPages": 3,
                                            "number": 0
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/page/{pageNo}/category")
    ResponseEntity<PagedModel<EntityModel<ProductDto>>> getAllProductsFromCategoryPaginated(
            @Parameter(
                    description = "Page number",
                    required = false)
            @PathVariable Optional<Integer> pageNo,
            @Parameter(
                    description = "Page size - number of products per page",
                    required = false)
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
            @Parameter(
                    description = "Sort field - the field that determines the order in which products appears on",
                    required = false)
            @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
            @Parameter(
                    description = "Sort direction - the field that determines the direction in which products appears on",
                    required = false)
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @Parameter(
                    description = "Category name - the field that determines the category from which products appears on",
                    required = true,
                    example = "Piwo")
            @RequestParam(value = "categoryName") String categoryName){
        int pageNumber = pageNo.orElse(1);
        Page<ProductDto> paginatedProducts = productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, categoryName);

        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(
                paginatedProducts,
                productDto -> EntityModel.of(productDto,
                        linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel()
                )
        );
        pagedModel.add(linkTo(methodOn(ProductController.class)
                .getAllProductsFromCategoryPaginated(pageNo, pageSize, sortField, sortDirection, categoryName))
                .withSelfRel());
        return ResponseEntity.ok(pagedModel);
    }


    @Operation(
            summary = "Get a product by its id",
            description = "Retrieve a product by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the product",
                    content =  @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)),
                            examples = @ExampleObject(value = """
                                        {
                                                        "id": 1,
                                                        "productName": "Pilsner",
                                                        "productPrice": 8.8,
                                                        "description": "„Ojciec” wszystkich lagerów, którego oryginalny smak od 175 lat pozostaje inspiracją i wzorem dla innych piw. W idealnym balansie łączy dwie natury: chmielową goryczkę i jęczmienną słodycz.",
                                                        "creationDate": "2024-02-14T11:33:54.478614",
                                                        "productQuantity": 20,
                                                        "categoryId": 1,
                                                        "categoryName": "Piwo",
                                                        "_links": {
                                                            "self": [
                                                                {
                                                                    "href": "http://localhost:8080/api/v1/products/1"
                                                                },
                                                                {
                                                                    "href": "http://localhost:8080/api/v1/products"
                                                                }
                                                            ]
                                                        }
                                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                        description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Product not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<ProductDto>> getProductById(
            @Parameter(
                    description = "id of product to be searched",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id){
        return productService.findProductById(id)
                .map(productDto -> {
                    EntityModel<ProductDto> productDtoEntityModel = EntityModel.of(productDto);
                    productDtoEntityModel.add(linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel());
                    productDtoEntityModel.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
                    return ResponseEntity.ok(productDtoEntityModel);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Delete a product",
            description = "Delete a product by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully, no content returned"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Product not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProductById(
            @Parameter(
                    description = "id of product to be deleted",
                    required = true,
                    example = "1")
            @PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Replace a product",
            description = "Update all details of product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                                        {
                                                        "id": 1,
                                                        "productName": "Super Pilsner urquell",
                                                        "productPrice": 10.6,
                                                        "description": "Super klasyczne czeskie piwo",
                                                        "creationDate": "2024-02-14T11:33:54.478614",
                                                        "productQuantity": 111,
                                                        "categoryId": 1,
                                                        "categoryName": "Piwo",
                                                        "_links": {
                                                            "self": {
                                                                "href": "http://localhost:8080/api/v1/products/1"
                                                            },
                                                            "all-products": {
                                                                "href": "http://localhost:8080/api/v1/products"
                                                            }
                                                        }
                                                    }
                                    """)
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
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Product not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<ProductDto>> replaceProduct(
            @Valid @RequestBody ProductDto productDto,
            @Parameter(
                    description = "id of product to be updated",
                    required = true,
                    example = "1")
            @PathVariable Long id){
        return productService.replaceProduct(id, productDto)
                .map( productDto1 -> {
                    EntityModel<ProductDto> entityModel = EntityModel.of(productDto1);
                    entityModel.add(linkTo(methodOn(ProductController.class).getProductById(productDto1.getId())).withSelfRel());
                    entityModel.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
                    return ResponseEntity.ok(entityModel);
                        }).orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Quantity of the product",
            description = "Get the quantity of the product by its id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Quantity of the product",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "integer", example = "10"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Product not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("{id}/quantity")
    ResponseEntity<?> getProductQuantity(
            @Parameter(
                    description = "id of the product to retrieve the quantity",
                    required = true,
                    example = "1")
            @PathVariable Long id){
        return productService.countQuantityOfProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Get products by searching text with pagination",
            description = "Retrieve a paginated list of products by searching text"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the list of products by searching text",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject("""
                                    {
                                        "_embedded": {
                                            "productDtoList": [
                                                {
                                                    "id": 2,
                                                    "productName": "IPA",
                                                    "productPrice": 10.5,
                                                    "description": "Piwo górnej fermentacji, charakteryzujące się mocnym chmielowym smakiem i wyrazistą goryczką.",
                                                    "creationDate": "2024-01-20T14:12:33.478614",
                                                    "productQuantity": 50,
                                                    "categoryId": 1,
                                                    "categoryName": "Piwo",
                                                    "_links": {
                                                        "self": {
                                                            "href": "http://localhost:8080/api/v1/products/2"
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        "_links": {
                                            "first": {
                                                "href": "http://localhost:8080/api/v1/products/search?searchText=piwo&pageSize=1&page=0&size=1&sort=productName,asc"
                                            },
                                            "self": [
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/search?searchText=piwo&pageSize=1&page=0&size=1&sort=productName,asc"
                                                },
                                                {
                                                    "href": "http://localhost:8080/api/v1/products/search?searchText=piwo&pageNo=1&pageSize=1&sortField=productName&sortDirection=ASC"
                                                }
                                            ],
                                            "next": {
                                                "href": "http://localhost:8080/api/v1/products/search?searchText=piwo&pageSize=1&page=1&size=1&sort=productName,asc"
                                            },
                                            "last": {
                                                "href": "http://localhost:8080/api/v1/products/search?searchText=piwo&pageSize=1&page=2&size=1&sort=productName,asc"
                                            }
                                        },
                                        "page": {
                                            "size": 1,
                                            "totalElements": 3,
                                            "totalPages": 3,
                                            "number": 0
                                        }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    ResponseEntity<PagedModel<EntityModel<ProductDto>>> findProductsByTextPaginated(
            @Parameter(
                    description = "Search text (example: piwo)",
                    required = true)
            @RequestParam String searchText,

            @Parameter(
                    description = "Page number (default: 1)",
                    required = false)
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,

            @Parameter(
                    description = "Page size - number of products per page (default: 6)",
                    required = false)
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,

            @Parameter(
                    description = "Sort field (e.g., productName, productPrice). Default: productName",
                    required = false)
            @RequestParam(value = "sortField", defaultValue = "productName") String sortField,

            @Parameter(
                    description = "Sort direction (ASC or DESC). Default: ASC",
                    required = false)
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection) {

        Page<ProductDto> paginatedProducts = productService.findProductsByTextPaginated(searchText, pageNo, pageSize, sortField, sortDirection);
        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(
                paginatedProducts,
                productDto -> EntityModel.of(productDto,
                        linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel()
                )
        );
        pagedModel.add(linkTo(methodOn(ProductController.class)
                .findProductsByTextPaginated(searchText, pageNo, pageSize, sortField, sortDirection))
                .withSelfRel());
        return ResponseEntity.ok(pagedModel);
    }
}
