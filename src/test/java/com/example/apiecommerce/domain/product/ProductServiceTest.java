package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private ProductDtoMapper productDtoMapperMock;

    @Mock
    private DataTimeProvider dataTimeProviderMock;

    private ProductService productService;


    @BeforeEach
    void init(){
        productService = new ProductService(productRepositoryMock, productDtoMapperMock, dataTimeProviderMock);
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
    void findAllPaginatedProducts() {
    }

    @Test
    void findProductsFromCategoryPaginated() {
    }

    @Test
    void findProductById() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void replaceProduct() {
    }

    @Test
    void countQuantityOfProduct() {
    }
}