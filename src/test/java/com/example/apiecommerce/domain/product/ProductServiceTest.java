package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.category.CategoryRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private ProductDtoMapper productDtoMapperMock;

    @Mock
    private DateTimeProvider dateTimeProviderMock;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    private ProductService productService;

    @BeforeEach
    void init(){
        productService = new ProductService(productRepositoryMock, categoryRepositoryMock, productDtoMapperMock, dateTimeProviderMock);
    }

    @Test
    void shouldAddNewProduct() {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Pilsner urquell");
        productDto.setProductPrice(8.60);
        productDto.setDescription("Klasyczne czeskie piwo");
        productDto.setProductQuantity(20L);
        productDto.setCategoryId(1L);

        LocalDateTime now = LocalDateTime.now();
        productDto.setCreationDate(now);

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product = new Product();
        product.setId(1L);
        product.setProductName(productDto.getProductName());
        product.setProductPrice(productDto.getProductPrice());
        product.setDescription(productDto.getDescription());
        product.setProductQuantity(productDto.getProductQuantity());
        product.setCategory(category);
        product.setCreationDate(now);

        Mockito.when(dateTimeProviderMock.getCurrentTime()).thenReturn(now);
        Mockito.when(productDtoMapperMock.map(productDto)).thenReturn(product);
        Mockito.when(productRepositoryMock.save(Mockito.any(Product.class))).thenReturn(product);

        //when
        productService.saveProduct(productDto);

        //then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepositoryMock).save(productCaptor.capture());

        Product productCaptorValue = productCaptor.getValue();
        assertEquals("Pilsner urquell", productCaptorValue.getProductName());
        assertEquals(8.60, productCaptorValue.getProductPrice());
        assertEquals("Klasyczne czeskie piwo", productCaptorValue.getDescription());
        assertEquals(20L, productCaptorValue.getProductQuantity());
        assertEquals(1L, productCaptorValue.getCategory().getId());
        assertEquals("Piwo", productCaptorValue.getCategory().getCategoryName());
        assertEquals(now, productCaptorValue.getCreationDate());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSavingProductWithNonExistingCategory() {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Pilsner urquell");
        productDto.setProductPrice(8.60);
        productDto.setDescription("Klasyczne czeskie piwo");
        productDto.setProductQuantity(20L);
        productDto.setCategoryId(111L);

        LocalDateTime now = LocalDateTime.now();
        productDto.setCreationDate(now);

        Mockito.when(dateTimeProviderMock.getCurrentTime()).thenReturn(now);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")).when(productDtoMapperMock).map(productDto);

        //when
        //then
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> productService.saveProduct(productDto));
        assertThat(exc.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertEquals("Category not found", exc.getReason());
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Pilsner urquell");
        productDto.setProductPrice(8.60);
        productDto.setDescription("Klasyczne czeskie piwo");
        productDto.setProductQuantity(20L);
        productDto.setCategoryId(1L);

        LocalDateTime now = LocalDateTime.now();
        Mockito.when(dateTimeProviderMock.getCurrentTime()).thenReturn(now);

        Product product = new Product();
        Mockito.when(productDtoMapperMock.map(productDto)).thenReturn(product);
        Mockito.doThrow(new RuntimeException("Database error")).when(productRepositoryMock).save(product);

        //when
        //then
        RuntimeException exc = assertThrows(RuntimeException.class, () -> productService.saveProduct(productDto));
        assertThat(exc.getMessage(), is("Database error"));
    }

    @Test
    void shouldFindTwoProducts() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product1.setCategory(category);
        product1.setCreationDate(now);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Zloty bazant");
        product2.setProductPrice(6.60);
        product2.setDescription("Klasyczne slowackie piwo");
        product2.setProductQuantity(10L);
        product2.setCategory(category);
        LocalDateTime now2 = LocalDateTime.now();
        product2.setCreationDate(now2);

        List<Product> productsList = new ArrayList<>();
        productsList.add(product1);
        productsList.add(product2);

        Mockito.when(productRepositoryMock.findAll()).thenReturn(productsList);

        Mockito.when(productDtoMapperMock.map(product1)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", now, 20L, 1L, "Piwo"));
        Mockito.when(productDtoMapperMock.map(product2)).thenReturn(new ProductDto(2L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, 1L, "Piwo"));

        //when
        List<ProductDto> allProducts = productService.findAllProducts();

        //then
        assertThat(allProducts, hasSize(2));
        assertThat(allProducts.get(0).getProductName(), is("Pilsner urquell"));
        assertThat(allProducts.get(0).getProductPrice(), is(8.60));
        assertThat(allProducts.get(0).getDescription(), is("Klasyczne czeskie piwo"));
        assertThat(allProducts.get(0).getProductQuantity(), is(20L));
        assertThat(allProducts.get(1).getProductName(), is("Zloty bazant"));
        assertThat(allProducts.get(1).getProductPrice(), is(6.60));
        assertThat(allProducts.get(1).getDescription(), is("Klasyczne slowackie piwo"));
        assertThat(allProducts.get(1).getProductQuantity(), is(10L));
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsExist() {
        //given
        List<Product> productsList = new ArrayList<>();

        Mockito.when(productRepositoryMock.findAll()).thenReturn(productsList);

        //when
        List<ProductDto> allProducts = productService.findAllProducts();

        //then
        assertThat(allProducts, hasSize(0));
    }

    @Test
    void shouldFindTwoPaginatedProducts() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product1.setCategory(category);
        product1.setCreationDate(now);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Zloty bazant");
        product2.setProductPrice(6.60);
        product2.setDescription("Klasyczne slowackie piwo");
        product2.setProductQuantity(10L);
        product2.setCategory(category);
        LocalDateTime now2 = LocalDateTime.now();
        product2.setCreationDate(now2);

        List<Product> productsList = new ArrayList<>();
        productsList.add(product1);
        productsList.add(product2);
        productsList.sort(Comparator.comparing(Product::getProductPrice));
        PageImpl<Product> page = new PageImpl<>(productsList);

        Mockito.when(productRepositoryMock.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Mockito.when(productDtoMapperMock.map(product1)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", now, 20L, 1L, "Piwo"));
        Mockito.when(productDtoMapperMock.map(product2)).thenReturn(new ProductDto(2L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, 1L, "Piwo"));

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> paginatedProducts = productService.findAllPaginatedProducts(pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(paginatedProducts.getTotalElements(), is(2L));
        assertThat(paginatedProducts.getContent().get(0).getProductName(), is("Zloty bazant"));
        assertThat(paginatedProducts.getContent().get(1).getProductName(), is("Pilsner urquell"));
    }

    @Test
    void shouldFindTwoProductsFromCategoryIgnoringCasePaginated() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product1.setCategory(category);
        product1.setCreationDate(now);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Zloty bazant");
        product2.setProductPrice(6.60);
        product2.setDescription("Klasyczne slowackie piwo");
        product2.setProductQuantity(10L);
        product2.setCategory(category);
        LocalDateTime now2 = LocalDateTime.now();
        product2.setCreationDate(now2);

        List<Product> productsList = new ArrayList<>();
        productsList.add(product1);
        productsList.add(product2);
        productsList.sort(Comparator.comparing(Product::getProductPrice));
        PageImpl<Product> page = new PageImpl<>(productsList);

        Mockito.when(categoryRepositoryMock.existsCategoryByCategoryNameIgnoreCase(eq("piwo"))).thenReturn(true);
        Mockito.when(productRepositoryMock.findAllByCategory_CategoryNameIgnoreCase(eq("piwo"), Mockito.any(Pageable.class))).thenReturn(page);

        Mockito.when(productDtoMapperMock.map(product1)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", now, 20L, 1L, "Piwo"));
        Mockito.when(productDtoMapperMock.map(product2)).thenReturn(new ProductDto(2L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, 1L, "Piwo"));

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> paginatedProducts = productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, "piwo");

        //then
        assertThat(paginatedProducts.getTotalElements(), is(2L));
        assertThat(paginatedProducts.getContent().get(0).getProductName(), is("Zloty bazant"));
        assertThat(paginatedProducts.getContent().get(1).getProductName(), is("Pilsner urquell"));
    }

    @Test
    void shouldFindZeroProductsFromCategoryIgnoringCasePaginated() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        List<Product> productsList = new ArrayList<>();
        PageImpl<Product> page = new PageImpl<>(productsList);

        Mockito.when(categoryRepositoryMock.existsCategoryByCategoryNameIgnoreCase(eq("piwo"))).thenReturn(true);
        Mockito.when(productRepositoryMock.findAllByCategory_CategoryNameIgnoreCase(eq("piwo"), Mockito.any(Pageable.class))).thenReturn(page);

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> paginatedProducts = productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, "piwo");

        //then
        assertThat(paginatedProducts.getTotalElements(), is(0L));
        assertThat(paginatedProducts.getContent(), is(empty()));
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotExist() {
        //given
        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        Mockito.when(categoryRepositoryMock.existsCategoryByCategoryNameIgnoreCase(eq("NieistniejącaKategoria"))).thenReturn(false);

        //when
        //then
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> productService.findProductsFromCategoryPaginated(pageNumber, pageSize, sortField, sortDirection, "NieistniejącaKategoria"));
        assertThat(exc.getMessage(), is("Category not found"));
    }

    @Test
    void shouldFindProductById() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Pilsner urquell");
        product.setProductPrice(8.60);
        product.setDescription("Klasyczne czeskie piwo");
        product.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product.setCategory(category);
        product.setCreationDate(now);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productDtoMapperMock.map(product)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", now, 20L, 1L, "Piwo"));
        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        ProductDto productDto = productService.findProductById(1L).orElseThrow();

        //then
        assertEquals(productDto.getProductName(), "Pilsner urquell");
        assertEquals(productDto.getProductQuantity(), 20L);
        assertEquals(productDto.getProductPrice(), 8.6, 0.01);
        assertEquals(productDto.getDescription(), "Klasyczne czeskie piwo");
        assertEquals(productDto.getCreationDate(), now);
        assertEquals(productDto.getCategoryName(), "Piwo");
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductNotExist() {
        //given
        Long nonExistingProductId = 1L;
        Mockito.when(productRepositoryMock.findById(nonExistingProductId)).thenReturn(Optional.empty());

        //when
        Optional<ProductDto> result = productService.findProductById(nonExistingProductId);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteProduct() {
        //given
        Long existingProductId = 1L;
        Mockito.when(productRepositoryMock.existsById(existingProductId)).thenReturn(true);

        //when
        productService.deleteProduct(existingProductId);

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).deleteById(Mockito.eq(existingProductId));
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotExistsProduct() {
        //given
        Long nonExistingProductId = 1L;
        Mockito.when(productRepositoryMock.existsById(nonExistingProductId)).thenReturn(false);

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(nonExistingProductId));

        //then
        assertThat(exc.getMessage(), is("Product not found"));
        Mockito.verify(productRepositoryMock, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void shouldReturnEmptyOptionalWhenReplaceNotExistProduct() {
        //given
        ProductDto productDto = new ProductDto();
        Long nonExistingProductId = 1L;
        Mockito.when(productRepositoryMock.findById(nonExistingProductId)).thenReturn(Optional.empty());

        //when
        Optional<ProductDto> result = productService.replaceProduct(nonExistingProductId, productDto);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReplaceProduct() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product1.setCategory(category);
        product1.setCreationDate(now);

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Zloty bazant");
        productDto.setProductPrice(6.60);
        productDto.setDescription("Klasyczne slowackie piwo");
        productDto.setProductQuantity(10L);
        productDto.setCategoryName("Piwo");
        productDto.setCategoryId(1L);
        LocalDateTime now2 = LocalDateTime.now();
        productDto.setCreationDate(now2);

        Product product = new Product(1L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, category);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product1));
        Mockito.when(categoryRepositoryMock.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productDtoMapperMock.map(product)).thenReturn(productDto);

        //when
        Optional<ProductDto> result = productService.replaceProduct(1L, productDto);

        //then
        assertTrue(result.isPresent());
        ProductDto resultProductDto = result.get();
        assertEquals("Zloty bazant", resultProductDto.getProductName());
        assertEquals(6.60, resultProductDto.getProductPrice());
        assertEquals(10L, resultProductDto.getProductQuantity());
        assertEquals("Klasyczne slowackie piwo", resultProductDto.getDescription());
        assertEquals(1L, resultProductDto.getCategoryId());
        assertEquals("Piwo", resultProductDto.getCategoryName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Zloty bazant");
        productDto.setProductPrice(6.60);
        productDto.setDescription("Klasyczne slowackie piwo");
        productDto.setProductQuantity(10L);
        productDto.setCategoryId(2L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setProductName("Pilsner urquell");

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(existingProduct));
        Mockito.when(categoryRepositoryMock.findById(2L)).thenReturn(Optional.empty());

        //when
        //then
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> productService.replaceProduct(1L, productDto));
        assertEquals("Category not found", exc.getMessage());
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductNotFound() {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Zloty bazant");

        Mockito.when(productRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when
        Optional<ProductDto> result = productService.replaceProduct(99L, productDto);

        //then
        assertTrue(result.isEmpty());
        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    void shouldReturnEmptyOptionalWhenCountQuantityNotExistProduct() {
        //given
        Long nonExistingProductId = 1L;
        Mockito.when(productRepositoryMock.existsById(nonExistingProductId)).thenReturn(false);

        //when
        Optional<Long> result = productService.countQuantityOfProduct(nonExistingProductId);

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).existsById(nonExistingProductId);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnQuantityOfProduct() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Pilsner urquell");
        product.setProductPrice(8.60);
        product.setDescription("Klasyczne czeskie piwo");
        product.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product.setCategory(category);
        product.setCreationDate(now);

        Mockito.when(productRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        Long result = productService.countQuantityOfProduct(1L).orElseThrow();

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).existsById(1L);
        Mockito.verify(productRepositoryMock, Mockito.times(1)).findById(1L);
        assertEquals(result, 20L);
    }

    @Test
    void shouldReturnZeroQuantityOfProduct() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(0L);

        Mockito.when(productRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        Long result = productService.countQuantityOfProduct(1L).orElseThrow();

        //then
        assertEquals(0L, result);
    }

    @Test
    void shouldReduceProductQuantityInDbByOne() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(5L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        productService.reduceProductQuantityInDbByOne(1L);

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).findById(1L);
        assertEquals(4L, product.getProductQuantity());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughProductQuantityAndTryReduceQuantityByOne() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(0L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        //then
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> productService.reduceProductQuantityInDbByOne(1L));
        assertEquals("Product is unavailable", exc.getMessage());
        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldIncreaseProductQuantityInDbByOne() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(5L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        productService.increaseProductQuantityInDbByOne(1L);

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).findById(1L);
        assertEquals(6L, product.getProductQuantity());
    }

    @Test
    void shouldUpdateProductQuantityByMinus5() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(5L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        productService.updateProductQuantityInDb(1L, 5);

        //then
        Mockito.verify(productRepositoryMock, Mockito.times(1)).findById(1L);
        assertEquals(0L, product.getProductQuantity());
    }

    @Test
    void shouldThrowExceptionWhenProductQuantityNotEnoughToUpdate() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(5L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when & then
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                productService.updateProductQuantityInDb(1L, 6)
        );
        assertEquals("Not enough quantity in stock", exc.getMessage());
        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        //given
        Mockito.when(productRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when & then
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () ->
                productService.updateProductQuantityInDb(99L, 5));
        assertEquals("Product not found", exc.getMessage());

        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldDoNothingWhenQuantityToChangeIsZero() {
        //given
        Product product = new Product();
        product.setId(1L);
        product.setProductQuantity(5L);

        Mockito.when(productRepositoryMock.findById(1L)).thenReturn(Optional.of(product));

        //when
        productService.updateProductQuantityInDb(1L, 0);

        //then
        Mockito.verify(productRepositoryMock, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldFindTwoProductsByGivenTextPaginated() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        LocalDateTime now = LocalDateTime.now();
        product1.setCategory(category);
        product1.setCreationDate(now);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Zloty bazant");
        product2.setProductPrice(6.60);
        product2.setDescription("Klasyczne slowackie piwo");
        product2.setProductQuantity(10L);
        product2.setCategory(category);
        LocalDateTime now2 = LocalDateTime.now();
        product2.setCreationDate(now2);

        List<Product> productsList = new ArrayList<>();
        productsList.add(product1);
        productsList.add(product2);
        productsList.sort(Comparator.comparing(Product::getProductPrice));
        PageImpl<Product> page = new PageImpl<>(productsList);

        String searchText = "piwo";

        Mockito.when(productRepositoryMock.findProductsBySearchText(Mockito.eq(searchText), Mockito.any(Pageable.class))).thenReturn(page);

        Mockito.when(productDtoMapperMock.map(product1)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", now, 20L, 1L, "Piwo"));
        Mockito.when(productDtoMapperMock.map(product2)).thenReturn(new ProductDto(2L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, 1L, "Piwo"));

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> productsResultPaginated = productService.findProductsByTextPaginated(searchText, pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(productsResultPaginated.getTotalElements(), is(2L));
        assertThat(productsResultPaginated.getContent().get(0).getProductName(), is("Zloty bazant"));
        assertThat(productsResultPaginated.getContent().get(1).getProductName(), is("Pilsner urquell"));
    }

    @Test
    void shouldFindZeroProductsByGivenTextPaginated() {
        //given
        List<Product> productsList = new ArrayList<>();
        PageImpl<Product> page = new PageImpl<>(productsList);

        String searchText = "piwo";

        Mockito.when(productRepositoryMock.findProductsBySearchText(Mockito.eq(searchText), Mockito.any(Pageable.class))).thenReturn(page);

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> productsResultPaginated = productService.findProductsByTextPaginated(searchText, pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(productsResultPaginated.getTotalElements(), is(0L));
        assertTrue(productsResultPaginated.getContent().isEmpty());
    }

    @Test
    void shouldReturnOnlyPageSizeElements() {
        //given
        List<Product> productsList = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(i);
            product.setProductName("Product " + i);
            product.setProductPrice(10.0 + i);
            product.setDescription("Description " + i);
            product.setProductQuantity(5L);
            product.setCreationDate(LocalDateTime.now());
            productsList.add(product);
        }

        PageImpl<Product> page = new PageImpl<>(productsList.subList(0, 3));

        Mockito.when(productRepositoryMock.findProductsBySearchText(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(page);
        Mockito.when(productDtoMapperMock.map(Mockito.any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            return new ProductDto(p.getId(), p.getProductName(), p.getProductPrice(), p.getDescription(), p.getCreationDate(), p.getProductQuantity(), 1L, "Piwo");
        });

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "ASC";

        //when
        Page<ProductDto> productsResultPaginated = productService.findProductsByTextPaginated("Product", pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(productsResultPaginated.getContent().size(), is(3));
    }


    @Test
    void shouldThrowExceptionWhenPageNumberIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.findProductsByTextPaginated("piwo", 0, 3, "productPrice", "ASC")
        );
    }

    @Test
    void shouldThrowExceptionWhenPageSizeIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.findProductsByTextPaginated("piwo", 1, 0, "productPrice", "ASC")
        );
    }

    @Test
    void shouldReturnEmptyPageWhenSearchTextIsNull() {
        Page<Product> emptyPage = Page.empty();
        Mockito.when(productRepositoryMock.findProductsBySearchText(Mockito.isNull(), Mockito.any(Pageable.class))).thenReturn(emptyPage);

        Page<ProductDto> result = productService.findProductsByTextPaginated(null, 1, 3, "productPrice", "ASC");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSortProductsByPriceDescending() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Pilsner urquell");
        product1.setProductPrice(8.60);
        product1.setDescription("Klasyczne czeskie piwo");
        product1.setProductQuantity(20L);
        product1.setCategory(category);
        product1.setCreationDate(LocalDateTime.now());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Zloty bazant");
        product2.setProductPrice(6.60);
        product2.setDescription("Klasyczne slowackie piwo");
        product2.setProductQuantity(10L);
        product2.setCategory(category);
        product2.setCreationDate(LocalDateTime.now());

        List<Product> productsList = List.of(product1, product2);
        PageImpl<Product> page = new PageImpl<>(productsList);

        String searchText = "piwo";

        Mockito.when(productRepositoryMock.findProductsBySearchText(Mockito.eq(searchText), Mockito.any(Pageable.class))).thenReturn(page);
        Mockito.when(productDtoMapperMock.map(product1)).thenReturn(new ProductDto(1L, "Pilsner urquell", 8.60, "Klasyczne czeskie piwo", product1.getCreationDate(), 20L, 1L, "Piwo"));
        Mockito.when(productDtoMapperMock.map(product2)).thenReturn(new ProductDto(2L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", product2.getCreationDate(), 10L, 1L, "Piwo"));

        int pageNumber = 1;
        int pageSize = 3;
        String sortField = "productPrice";
        String sortDirection = "DESC";

        //when
        Page<ProductDto> productsResultPaginated = productService.findProductsByTextPaginated(searchText, pageNumber, pageSize, sortField, sortDirection);

        //then
        assertThat(productsResultPaginated.getTotalElements(), is(2L));
        assertThat(productsResultPaginated.getContent().get(0).getProductName(), is("Pilsner urquell"));
        assertThat(productsResultPaginated.getContent().get(1).getProductName(), is("Zloty bazant"));
    }
}