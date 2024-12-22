package com.example.apiecommerce.domain.category;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void findAllCategories() {

    }

    @Test
    void replaceCategory() {
    }

    @Test
    void deleteCategory() {
    }
}