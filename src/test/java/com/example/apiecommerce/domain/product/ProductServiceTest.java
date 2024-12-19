package com.example.apiecommerce.domain.product;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
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
    void findAllProducts() {
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