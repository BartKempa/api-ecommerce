package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<ProductDto>findAllPaginatedProducts(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber -1, pageSize, sort);
        return productRepository.findAll(pageable)
                .map(productDtoMapper::map);
    }

    public Page<ProductDto> findProductsFromCategoryPaginated(int pageNumber, int pageSize, String sortField, String sortDirection, String categoryName){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return productRepository.findAllByCategory_CategoryNameIgnoreCase(categoryName, pageable)
                .map(productDtoMapper::map);
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

    public Optional<Long> countQuantityOfProduct(Long productId){
        if (!productRepository.existsById(productId)){
            return Optional.empty();
        }
        return productRepository.findById(productId).map(Product::getProductQuantity);
    }

/*    public void updateProductQuantity(){
       TODO - aktualizacja ilośi produktu po dokonaniu zamówienia
    }*/
}
