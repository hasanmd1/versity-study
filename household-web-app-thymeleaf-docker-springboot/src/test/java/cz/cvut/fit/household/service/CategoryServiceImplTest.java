package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.category.CategoryCreationDTO;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    Category subcategory2 = new Category(3L, household1, new ArrayList<>(), null, new ArrayList<>(), "subcategory2", "subcategory2 description");
    Category subcategory1 = new Category(4L, household1, new ArrayList<>(), null, new ArrayList<>(), "subcategory1", "subcategory1 description");

    Category category = new Category(1L, household1, new ArrayList<>(), null, Arrays.asList(subcategory2, subcategory1), "category1", "category1 description");
    List<Category> category1 = Collections.singletonList(category);
    CategoryCreationDTO categoryCreationDTO = new CategoryCreationDTO("category1", "category1 description");

    @Before
    public void setUp(){
        subcategory1.setMainCategory(category);
        subcategory2.setMainCategory(category);
        household1.setCategory(category1);

        when(categoryRepository.findAll()).thenReturn(category1);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.getCategoryById(category.getId())).thenReturn(category);
        //when(categoryService.addCategory(categoryCreationDTO, household1, null)).thenReturn(category);
        doNothing().when(categoryRepository).deleteById(category.getId());

    }

    @Test
    public void addCategory() {
        categoryService.addCategory(categoryCreationDTO, household1, null);
        verify(categoryRepository,times(1)).save(any(Category.class));
    }

    @Test
    public void findAllCategory() {
        List<Category> result = categoryService.findAllCategory();
        assertEquals(result, category1);
    }

    @Test
    public void findAllSubCategory() {
        List<Category> result = categoryService.findAllSubCategory(category);
        assertEquals(result,category.getSubCategory());
    }

    @Test
    public void findCategoryById() {
        Optional<Category> result =  categoryService.findCategoryById(category.getId());
        assertEquals(result, Optional.of(category));
    }

    @Test
    public void deleteCategoryById() {
        categoryService.deleteCategoryById(category.getId());
        verify(categoryRepository, times(1)).deleteById(category.getId());
    }

    @Test
    public void updateCategory() {
        Category updatedCategory = new Category(category.getId(),household1,new ArrayList<>(),null,Arrays.asList(subcategory2, subcategory1),"new_title","new_description");
        CategoryCreationDTO updatedCategoryCreationDTO = new CategoryCreationDTO("new_title", "new_description");

        categoryService.updateCategory(category.getId(),updatedCategoryCreationDTO);

        ArgumentCaptor<Category> argument = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(argument.capture());
        assertEquals(updatedCategory.getTitle(), argument.getValue().getTitle());
        assertEquals(category.getId(), argument.getValue().getId());
        assertEquals(category.getMainCategory(), argument.getValue().getMainCategory());
        assertEquals(category.getSubCategory(), argument.getValue().getSubCategory());
        assertEquals(category.getDescription(), argument.getValue().getDescription());
        assertEquals(category.getHouseHolD(), argument.getValue().getHouseHolD());
        assertEquals(category.getItems(), argument.getValue().getItems());
        assertEquals(category.getCategoryPath(), argument.getValue().getCategoryPath());
    }

    @Test
    public void getCategoryById() {
        Category result =  categoryService.getCategoryById(category.getId());
        assertEquals(result, category);
    }

    @Test
    public void findWithHousehold() {
        List<Category> result = categoryService.findwithHousehold(household1.getId());
        assertEquals(result,category1);
    }
}