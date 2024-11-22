package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.product.ProductService;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/product")
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




}
