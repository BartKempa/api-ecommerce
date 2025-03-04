package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    private CategoryService categoryService;

    @BeforeEach
    void init(){
        categoryService = new CategoryService(categoryRepositoryMock);
    }

    @Test
    void addCategory() {
        //given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Piwo");

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Piwo");

        Mockito.when(categoryRepositoryMock.save(Mockito.any(Category.class))).thenReturn(category);

        //when
        CategoryDto categoryDtoResult = categoryService.addCategory(categoryDto);

        //then
        assertNotNull(categoryDtoResult);
        assertEquals("Piwo", categoryDtoResult.getCategoryName());
        assertEquals(1L, categoryDtoResult.getId());

        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryRepositoryMock).save(categoryArgumentCaptor.capture());

        Category result = categoryArgumentCaptor.getValue();
        Mockito.verify(categoryRepositoryMock, Mockito.times(1)).save(Mockito.any(Category.class));
        assertEquals("Piwo", result.getCategoryName());
    }

    @Test
    void shouldFindTwoCategories() {
        //given
        Category category1 = new Category();
        category1.setId(1L);
        category1.setCategoryName("Piwo");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setCategoryName("Wino");

        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        Mockito.when(categoryRepositoryMock.findAll()).thenReturn(categories);

        //when
        List<CategoryDto> result = categoryService.findAllCategories();

        //then
        assertEquals(2, result.size());
        assertEquals("Piwo", result.get(0).getCategoryName());
        assertEquals("Wino", result.get(1).getCategoryName());
    }

    @Test
    void shouldReturnEmptyListWhenCategoriesNotExist() {
        //given
        List<Category> categories = new ArrayList<>();

        Mockito.when(categoryRepositoryMock.findAll()).thenReturn(categories);

        //when
        List<CategoryDto> result = categoryService.findAllCategories();

        //then
        assertEquals(0, result.size());
    }

    @Test
    void shouldCallFindAllOnce() {
        //given
        Mockito.when(categoryRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        //when
        categoryService.findAllCategories();

        //then
        Mockito.verify(categoryRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    void shouldReplaceCategory() {
        //given
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Wino");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Piwo");

        Category category1 = new Category();
        category1.setId(1L);
        category1.setCategoryName("Piwo");

        Mockito.when(categoryRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepositoryMock.save(Mockito.any(Category.class))).thenReturn(category1);

        //when
        Optional<CategoryDto> result = categoryService.replaceCategory(1L, categoryDto);

        //then
        assertTrue(result.isPresent());
        CategoryDto resultCategoryDto = result.get();
        assertEquals("Piwo", resultCategoryDto.getCategoryName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNotExistCategoryAndTryUpdateIt() {
        //given
        CategoryDto categoryDto = new CategoryDto();
        Long nonExistingCategory = 1L;

        Mockito.when(categoryRepositoryMock.existsById(nonExistingCategory)).thenReturn(false);

        //when
        Optional<CategoryDto> result = categoryService.replaceCategory(1L, categoryDto);

        //then
        assertTrue(result.isEmpty());
        Mockito.verify(categoryRepositoryMock, Mockito.times(1)).existsById(nonExistingCategory);
        Mockito.verifyNoMoreInteractions(categoryRepositoryMock);
    }

    @Test
    void shouldThrowNotFoundExceptionTryDeleteNonExistCategory() {
        //given
        Long nonExistingCategory = 1L;

        Mockito.when(categoryRepositoryMock.existsById(nonExistingCategory)).thenReturn(false);

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(nonExistingCategory));

        //then
        assertEquals(exc.getMessage(), "Category not found");
        Mockito.verify(categoryRepositoryMock, Mockito.never()).deleteById(nonExistingCategory);
    }

    @Test
    void shouldDeleteCategory() {
        //given
        Long existingCategory = 1L;

        Mockito.when(categoryRepositoryMock.existsById(existingCategory)).thenReturn(true);

        //when
        categoryService.deleteCategory(existingCategory);

        //then
        Mockito.verify(categoryRepositoryMock, Mockito.times(1)).deleteById(existingCategory);
    }
}