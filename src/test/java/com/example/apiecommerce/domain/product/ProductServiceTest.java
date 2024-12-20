package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DataTimeProvider;
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
    private DataTimeProvider dataTimeProviderMock;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    private ProductService productService;


    @BeforeEach
    void init(){
        productService = new ProductService(productRepositoryMock, categoryRepositoryMock, productDtoMapperMock, dataTimeProviderMock);
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

        Mockito.when(dataTimeProviderMock.getCurrentTime()).thenReturn(now);
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

        Mockito.when(dataTimeProviderMock.getCurrentTime()).thenReturn(now);
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
        Mockito.when(dataTimeProviderMock.getCurrentTime()).thenReturn(now);

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

        Mockito.when(productRepositoryMock.existsById(1L)).thenReturn(true);
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
        Mockito.when(productRepositoryMock.existsById(nonExistingProductId)).thenReturn(false);

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
        Mockito.when(productRepositoryMock.existsById(nonExistingProductId)).thenReturn(false);

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

        Mockito.when(productRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(productDtoMapperMock.map(productDto)).thenReturn(product);
        Mockito.when(productRepositoryMock.save(product)).thenReturn(product);
        Mockito.when(productDtoMapperMock.map(product)).thenReturn(new ProductDto(1L, "Zloty bazant", 6.60, "Klasyczne slowackie piwo", now2, 10L, 1L, "Piwo"));

        //when
        Optional<ProductDto> result = productService.replaceProduct(1L, productDto);

        //then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepositoryMock).save(productArgumentCaptor.capture());

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
    void countQuantityOfProduct() {
    }
}