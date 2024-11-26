package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;

    public ProductService(ProductRepository productRepository, ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
    }

    @Transactional
    public ProductDto saveProduct(ProductDto productDto){
        Product productToSave = productDtoMapper.map(productDto);
        Product savedProduct = productRepository.save(productToSave);
        return productDtoMapper.map(savedProduct);
    }

    public List<ProductDto> findAllProducts(){
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .map(productDtoMapper::map)
                .toList();
    }

    public Optional<ProductDto> findProductById(Long productId){
        if (!productRepository.existsById(productId)){
            return Optional.empty();
        }
        return productRepository.findById(productId).map(productDtoMapper::map);
    }

    @Transactional
    public void deleteProduct(Long productId){
        if (!productRepository.existsById(productId)){
            throw new EntityNotFoundException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public Optional<ProductDto> replaceProduct(Long productId, ProductDto productDto){
        if (!productRepository.existsById(productId)){
            return Optional.empty();
        }
        productDto.setId(productId);
        Product productToUpdate = productDtoMapper.map(productDto);
        Product updatedProduct = productRepository.save(productToUpdate);
        return Optional.of(productDtoMapper.map(updatedProduct));
    }
}
