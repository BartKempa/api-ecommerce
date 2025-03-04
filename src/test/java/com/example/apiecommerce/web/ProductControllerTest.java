package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;


    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminAddProduct() throws Exception {
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

        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Pilsner urquell"))
                .andExpect(jsonPath("$.productPrice").value(8.60))
                .andExpect(jsonPath("$.description").value("Klasyczne czeskie piwo"))
                .andExpect(jsonPath("$.creationDate").isNotEmpty())
                .andExpect(jsonPath("$.productQuantity").value(20L))
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Piwo"))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ProductDto productDtoResult = objectMapper.readValue(contentAsString, ProductDto.class);

        //then
        productRepository.existsById(productDtoResult.getId());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedWhenUserAddingProductWithoutAuthorization() throws Exception {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setCategoryId(1L);

        //when
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenAddingProductWithInvalidData() throws Exception {
        //given
        ProductDto productDto = new ProductDto();

        //when
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input"));
    }

    @Test
    void shouldFailWhenAddingProductWithoutAuthentication() throws Exception {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Pilsner urquell");
        productDto.setProductPrice(8.60);
        productDto.setDescription("Klasyczne czeskie piwo");
        productDto.setProductQuantity(20L);
        productDto.setCategoryId(1L);

        //when
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenAddingProductWithNonExistentCategory() throws Exception {
        //given
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Pilsner urquell");
        productDto.setProductPrice(8.60);
        productDto.setDescription("Klasyczne czeskie piwo");
        productDto.setProductQuantity(20L);
        productDto.setCategoryId(999L);

        //when
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

/*    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getAllProducts() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$[0].productName").value("Pilsner"))
                .andExpect(jsonPath("$[18].productPrice").value(250.00))
                .andExpect(jsonPath("$[19].description").value("Elegancki szampan o owocowym aromacie."));
    }*/

    @Test
    void shouldFailWhenUserIsNotAuthenticated() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
/*

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnEmptyListWhenNoProductsInDatabase() throws Exception {
        //given & when & then
        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
*/
/*

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetAllProductsPaginated() throws Exception {
        // given
        long pageNo = 1L;
        Integer pageSize = 5;

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/page/{pageNo}", pageNo)
                        .param("pageSize", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();



        String contentAsString = mvcResult.getResponse().getContentAsString();

        Page<ProductDto> productPage = objectMapper.readValue(contentAsString, new TypeReference<>() {});

        // then
        assertEquals(pageSize, productPage.getSize());
        assertFalse(productPage.getContent().isEmpty());
    }
*/


    @Test
    void getAllProductsFromCategoryPaginated() {
    }

    @Test
    void getProductById() {
    }

    @Test
    void deleteProductById() {
    }

    @Test
    void replaceProduct() {
    }

    @Test
    void getProductQuantity() {
    }

    @Test
    void findProductsByTextPaginated() {
    }
}