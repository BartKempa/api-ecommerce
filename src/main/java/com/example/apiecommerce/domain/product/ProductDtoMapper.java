package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.category.CategoryRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductDtoMapper {
    private final CategoryRepository categoryRepository;

    ProductDtoMapper(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    ProductDto map(Product product){
        if (product == null){
            return null;
        }
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductName(product.getProductName());
        productDto.setProductPrice(product.getProductPrice());
        productDto.setDescription(product.getDescription());
        productDto.setCreationDate(product.getCreationDate());
        productDto.setProductQuantity(product.getProductQuantity());
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryName(product.getCategory().getCategoryName());
        return productDto;
    }

    Product map(ProductDto productDto){
        if (productDto == null){
            return null;
        }
        Product product = new Product();
        product.setId(productDto.getId());
        product.setProductName(productDto.getProductName());
        product.setProductPrice(productDto.getProductPrice());
        product.setDescription(productDto.getDescription());
        product.setCreationDate(LocalDateTime.now());
        product.setProductQuantity(productDto.getProductQuantity());
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        product.setCategory(category);
        return product;
    }
}
