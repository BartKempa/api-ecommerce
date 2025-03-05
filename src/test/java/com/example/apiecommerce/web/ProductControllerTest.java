package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.product.ProductRepository;
import com.example.apiecommerce.domain.product.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetAllProducts() throws Exception {
        // given & when & then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productDtoes.length()").value(20))
                .andExpect(jsonPath("$._embedded.productDtoes[0].productName").value("Pilsner"))
                .andExpect(jsonPath("$._embedded.productDtoes[18].productPrice").value(250.00))
                .andExpect(jsonPath("$._embedded.productDtoes[19].description").value("Elegancki szampan o owocowym aromacie."))
                .andExpect(jsonPath("$._embedded.productDtoes[0]._links.self.href").value("http://localhost/api/v1/products/1"));
    }

    @Test
    void shouldFailWhenUserIsNotAuthenticated() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnEmptyListWhenNoProductsInDatabase() throws Exception {
        // given & when & then
        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetAllProductsPaginated() throws Exception {
        // given
        long pageNo = 1L;

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/page/{pageNo}", pageNo)
                        .param("pageSize", "2")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(contentAsString).contains("_embedded\" : {");
        assertThat(contentAsString).contains("\"productDtoes\" : [ {");
        assertThat(contentAsString).contains("\"id\" : 9,");
        assertThat(contentAsString).contains("\"productName\" : \"Burbon Jack Daniels\",");
        assertThat(contentAsString).contains("\"productPrice\" : 130.0,");
        assertThat(contentAsString).contains("\"_links\" : {");
        assertThat(contentAsString).contains("\"self\" : {");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetAllProductsFromCategoryPaginated() throws Exception {
        // given
        long pageNo = 1L;

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/page/{pageNo}/category", pageNo)
                        .param("categoryName", "Piwo")
                        .param("pageSize", "2")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(contentAsString).contains("\"_embedded\" : {");
        assertThat(contentAsString).contains("\"productDtoes\" : [");
        assertThat(contentAsString).contains("\"id\" : 2");
        assertThat(contentAsString).contains("\"productName\" : \"IPA\"");
        assertThat(contentAsString).contains("\"productPrice\" : 10.5");
        assertThat(contentAsString).contains("\"_links\" : {");
        assertThat(contentAsString).contains("\"self\" : {");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundForInvalidCategory() throws Exception {
        //given
        long pageNo = 1L;

        //when
        mockMvc.perform(get("/api/v1/products/page/{pageNo}/category", pageNo)
                        .param("categoryName", "NiepoprawnaKategoria")
                        .param("pageSize", "2")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldSortProductsDescending() throws Exception {
        //given
        long pageNo = 1L;

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/page/{pageNo}/category", pageNo)
                        .param("categoryName", "Piwo")
                        .param("pageSize", "2")
                        .param("sortField", "productName")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        //then
        assertThat(contentAsString).contains("\"productName\" : \"Porter\",");
        assertThat(contentAsString).contains("\"productName\" : \"Pilsner\",");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnSecondPageOfProducts() throws Exception {
        //given
        long pageNo = 3L;

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/page/{pageNo}/category", pageNo)
                        .param("categoryName", "Piwo")
                        .param("pageSize", "1")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        //then
        assertThat(contentAsString).contains("\"number\" : 2");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetProductById() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when
        mockMvc.perform(get("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Pilsner"))
                .andExpect(jsonPath("$.productPrice").value(8.8))
                .andExpect(jsonPath("$.productQuantity").value(20));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundForNonExistingProductById() throws Exception {
        //given
        long nonExistingId = 999L;
        assertFalse(productRepository.existsById(nonExistingId));

        //when
        mockMvc.perform(get("/api/v1/products/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminDeleteProductById() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when
        mockMvc.perform(delete("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(productRepository.existsById(productId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedDeletingProductByIdWithoutAuthorization() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when & then
        mockMvc.perform(delete("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistingProduct() throws Exception {
        //given
        long nonExistingProductId = 999L;
        assertFalse(productRepository.existsById(nonExistingProductId));

        //when & then
        mockMvc.perform(delete("/api/v1/products/{id}", nonExistingProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletingProductWithoutLogin() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when & then
        mockMvc.perform(delete("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminReplaceProduct() throws Exception {
        // given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        ProductDto productDto = new ProductDto();
        productDto.setProductName("Super piwo");
        productDto.setProductPrice(5.50);
        productDto.setDescription("Super dobre piwo");
        productDto.setProductQuantity(100L);
        productDto.setCategoryId(1L);

        //when
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();

        //then
        assertThat(result).contains("Super piwo");
        assertThat(result).contains("Super dobre piwo");
        assertThat(result).contains("100");
        assertThat(result).contains("5.5");
        assertThat(result).contains("http://localhost/api/v1/products/1");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailedUpdatingProductWithoutAuthorization() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));
        ProductDto productDto = new ProductDto();

        //when & then
        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenReplacingNonExistentProduct() throws Exception {
        //given
        long nonExistentProductId = 999L;
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Super piwo");
        productDto.setProductPrice(5.50);
        productDto.setDescription("Super dobre piwo");
        productDto.setProductQuantity(100L);
        productDto.setCategoryId(1L);

        //when & then
        mockMvc.perform(put("/api/v1/products/{id}", nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnBadRequestWhenProductDataIsInvalid() throws Exception {
        //given
        long productId = 1L;
        ProductDto productDto = new ProductDto();

        //when & then
        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldGetProductQuantity() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when
        mockMvc.perform(get("/api/v1/products/{id}/quantity", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("20"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        //given
        long nonExistingProductId = 999L;
        assertFalse(productRepository.existsById(nonExistingProductId));

        //when & then
        mockMvc.perform(get("/api/v1/products/{id}/quantity", nonExistingProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        //given
        long productId = 1L;
        assertTrue(productRepository.existsById(productId));

        //when & then
        mockMvc.perform(get("/api/v1/products/{id}/quantity", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFindProductsByTextPaginated() throws Exception {
        //given & when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/search")
                        .param("searchText", "piwo")
                        .param("pageSize", "2")
                        .param("pageNo", "1")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(contentAsString).contains("\"_embedded\" : {");
        assertThat(contentAsString).contains("\"productDtoes\" : [");
        assertThat(contentAsString).contains("\"id\" : 2");
        assertThat(contentAsString).contains("\"productName\" : \"IPA\"");
        assertThat(contentAsString).contains("\"productPrice\" : 10.5");
        assertThat(contentAsString).contains("\"_links\" : {");
        assertThat(contentAsString).contains("\"self\" : {");
        assertThat(contentAsString).contains("\"totalElements\" : 3");
        assertThat(contentAsString).contains("\"totalPages\" : 2");
        assertThat(contentAsString).contains("\"number\" : 0");
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnEmptyListWhenNoProductsMatch() throws Exception {
        //given & when
        mockMvc.perform(get("/api/v1/products/search")
                        .param("searchText", "nonexistent")
                        .param("pageSize", "2")
                        .param("pageNo", "1")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldReturnBadRequestForInvalidPaginationParams() throws Exception {
        //given & when
        mockMvc.perform(get("/api/v1/products/search")
                        .param("searchText", "piwo")
                        .param("pageSize", "-1")
                        .param("pageNo", "0")
                        .param("sortField", "productName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}