package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.category.CategoryRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDtoMapper productDtoMapper;
    private final DateTimeProvider dateTimeProvider;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductDtoMapper productDtoMapper, DateTimeProvider dateTimeProvider) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productDtoMapper = productDtoMapper;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Transactional
    public ProductDto saveProduct(ProductDto productDto){
        LocalDateTime now = dateTimeProvider.getCurrentTime();
        productDto.setCreationDate(now);
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
        if (!categoryRepository.existsCategoryByCategoryNameIgnoreCase(categoryName)){
            throw new EntityNotFoundException("Category not found");
        }
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return productRepository.findAllByCategory_CategoryNameIgnoreCase(categoryName, pageable)
                .map(productDtoMapper::map);
    }

    public Optional<ProductDto> findProductById(Long productId){
        return productRepository.findById(productId)
                .map(productDtoMapper::map);
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

    @Transactional
    public void reduceProductQuantityInDbByOne(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (product.getProductQuantity() < 1){
            throw new IllegalArgumentException("Product is unavailable");
        }
        product.setProductQuantity(product.getProductQuantity() - 1);
        productRepository.save(product);
    }

    @Transactional
    public void increaseProductQuantityInDbByOne(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setProductQuantity(product.getProductQuantity() + 1);
        productRepository.save(product);
    }

    @Transactional
    public void updateProductQuantityInDb(Long productId, long quantityToChange){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (product.getProductQuantity() < quantityToChange){
            throw new IllegalArgumentException("Not enough quantity in stock");
        }
        product.setProductQuantity(product.getProductQuantity() - quantityToChange);
        productRepository.save(product);
    }
}
