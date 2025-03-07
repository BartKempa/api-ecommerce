package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.Category;
import com.example.apiecommerce.domain.category.CategoryRepository;
import com.example.apiecommerce.domain.category.dto.CategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminAddCategory() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Piwo");

        //when
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Piwo"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserAddCategoryWithoutAuthorization() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();

        //when & then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldFailWhenCategoryNameIsInvalid() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("");

        //when
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFailWhenAddingCategoryWithoutAuthentication() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Piwo");

        //when
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldUserGetAllCategories() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].categoryName").value("Piwo"))
                .andExpect(jsonPath("$[2].categoryName").value("Whisky"));
    }

    @Test
    void shouldFailWhenUserIsNotAuthenticatedAndGetCategories() throws Exception {
        //given & when & then
        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldAdminReplaceCategory() throws Exception {
        //given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Super piwo");
        categoryRepository.existsById(1L);

        //when
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isNoContent());

        //then
        Category categoryResult = categoryRepository.findById(1L).orElseThrow();
        assertTrue(categoryResult.getCategoryName().contains("Super piwo"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserUpdateCategoryWithoutAuthorization() throws Exception {
        //given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Super piwo");
        categoryRepository.existsById(1L);

        //when & then
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdatingNonExistentCategory() throws Exception {
        //given
        long nonExistentCategoryId = 999L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Super piwo");

        //when & then
        mockMvc.perform(put("/api/v1/categories/{id}", nonExistentCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdatingCategoryWithInvalidName() throws Exception {
        //given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("");

        //when & then
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFailWhenUpdatingCategoryWithoutAuthentication() throws Exception {
        //given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Super piwo");

        //when & then
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void deleteCategory() throws Exception {
        //given
        long categoryId = 1L;
        categoryRepository.existsById(1L);

        //when
        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        assertFalse(categoryRepository.existsById(1L));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void shouldFailWhenUserDeleteCategoryWithoutAuthorization() throws Exception {
        //given
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryRepository.existsById(1L);

        //when & then
        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistentCategory() throws Exception {
        //given
        long nonExistentCategoryId = 999L;

        //when & then
        mockMvc.perform(delete("/api/v1/categories/{id}", nonExistentCategoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailWhenDeletingCategoryWithoutAuthentication() throws Exception {
        //given
        long categoryId = 1L;

        //when & then
        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}