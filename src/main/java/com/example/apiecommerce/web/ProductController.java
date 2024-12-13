package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.product.ProductService;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto){
        ProductDto savedProduct = productService.saveProduct(productDto);
        URI savedProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
        return ResponseEntity.created(savedProductUri).body(savedProduct);
    }

    @GetMapping
    List<ProductDto> getAllProducts(){
        return productService.findAllProducts();
    }

    @GetMapping("/page/{pageNo}")
    Page<ProductDto> getAllProductsPaginated(@PathVariable Optional<Integer> pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                             @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
                                             @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection){
        int pageNumber = pageNo.orElse(1);
        return productService.findAllPaginatedProducts(pageNumber, pageSize, sortField, sortDirection);
    }

    @GetMapping("/page/{pageNo}/category/{categoryName}")
    Page<ProductDto> getAllProductsFromCategoryPaginated(@PathVariable Optional<Integer> pageNo,
                                                         @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                                         @RequestParam(value = "sortField", defaultValue = "productName") String sortField,
                                                         @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
                                                         @PathVariable String categoryName){
        int pageNumber = pageNo.orElse(1);
        return productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, categoryName);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProductById(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceProduct(@PathVariable Long id, @RequestBody ProductDto productDto){
        return productService.replaceProduct(id, productDto)
                .map(c -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
