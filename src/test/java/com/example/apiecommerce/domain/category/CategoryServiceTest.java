package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

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

        PageImpl<Category> categoryPage = new PageImpl<>(categories);

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
    void deleteCategory() {
    }
}