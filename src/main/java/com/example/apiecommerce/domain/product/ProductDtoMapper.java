package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.category.CategoryRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
class ProductDtoMapper {
    private final CategoryRepository categoryRepository;
    private final DateTimeProvider dateTimeProvider;

    ProductDtoMapper(CategoryRepository categoryRepository, DateTimeProvider dateTimeProvider) {

        this.categoryRepository = categoryRepository;
        this.dateTimeProvider = dateTimeProvider;
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
        if (productDto.getId() != null) {
            product.setId(productDto.getId());
        }
        product.setId(productDto.getId());
        product.setProductName(productDto.getProductName());
        product.setProductPrice(productDto.getProductPrice());
        product.setDescription(productDto.getDescription());
        product.setCreationDate(dateTimeProvider.getCurrentTime());
        product.setProductQuantity(productDto.getProductQuantity());
        if (productDto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        product.setCategory(category);
        return product;
    }
}
