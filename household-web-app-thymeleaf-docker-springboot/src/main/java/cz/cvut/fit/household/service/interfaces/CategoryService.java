package cz.cvut.fit.household.service.interfaces;

import com.google.common.util.concurrent.AtomicDouble;
import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.category.CategoryCreationDTO;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.item.Item;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category addCategory(CategoryCreationDTO categoryCreationDTO, Household household, Category mainCategory);

    List<Category> findAllCategory();

    List<Category> findAllSubCategory(Category category);

    Optional<Category> findCategoryById(Long id);

    void deleteCategoryById(Long id);

    Category updateCategory(Long categoryId, CategoryCreationDTO updatedCategory);

    Category getCategoryById(Long id);

    List<Category>findwithHousehold(Long householdId);

    void calculateQuantity(List<Item> items, AtomicDouble kilo, AtomicDouble totalKilo,
                                   AtomicDouble piece, AtomicDouble totalPiece,
                                   AtomicDouble liter, AtomicDouble totalLiter);
}
