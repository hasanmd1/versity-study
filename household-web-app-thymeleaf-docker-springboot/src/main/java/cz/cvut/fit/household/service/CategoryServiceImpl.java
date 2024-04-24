package cz.cvut.fit.household.service;

import com.google.common.util.concurrent.AtomicDouble;
import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.category.CategoryCreationDTO;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.CategoryRepository;
import cz.cvut.fit.household.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(CategoryCreationDTO updatedCategory, Household household, Category mainCategory) {
        Category category = new Category();

        category.setTitle(updatedCategory.getTitle());
        category.setDescription(updatedCategory.getDescription());
        category.setHouseHolD(household);
        category.setMainCategory(mainCategory);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllSubCategory(Category mainCategory) {
        List<Category> allSubCategory = mainCategory.getSubCategory();
        List<Category> resultList = new ArrayList<>();

        for(Category subCategory : allSubCategory){
            resultList.add(subCategory);
            resultList.addAll(findAllSubCategory(subCategory));
        }

        return resultList;
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryCreationDTO updatedCategory) {
        Category category = findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException("Category with id: " + categoryId + " doesn't exist"));

        category.setTitle(updatedCategory.getTitle());
        category.setDescription(updatedCategory.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id){
        return categoryRepository.getCategoryById(id);
    }

    @Override
    public List<Category>findwithHousehold(Long householdId){
        List<Category>category = categoryRepository.findAll();
        Boolean check;
        List<Category>categories = new ArrayList<>();
        for(Category category1: category){
            if(category1.getHouseHolD() == null){
                check = findHousehold(category1, householdId);
                if(check.equals(true)){
                    categories.add(category1);
                }
            }
            else if(category1.getHouseHolD().getId().equals(householdId)) {
                categories.add(category1);
            }
        }
        return categories;

    }

    private boolean findHousehold(Category category, Long householdId) {
        while(category.getHouseHolD() == null){
            category = category.getMainCategory();
        }

        return category.getHouseHolD().getId().equals(householdId);
    }

    public void calculateQuantity(List<Item> items, AtomicDouble kilo, AtomicDouble totalKilo,
                                  AtomicDouble piece, AtomicDouble totalPiece,
                                  AtomicDouble liter, AtomicDouble totalLiter){
        for(Item item: items){
            if(item.getQuantityType() != null && item.getQuantityType().getType().equals("kg")){
                kilo.addAndGet(item.getCurrentQuantity());
                totalKilo.addAndGet(item.getMaxQuantity());
            }else if(item.getQuantityType() != null && item.getQuantityType().getType().equals("l")){
                liter.addAndGet(item.getCurrentQuantity());
                totalLiter.addAndGet(item.getMaxQuantity());
            }else if(item.getQuantityType() != null && item.getQuantityType().getType().equals("p")){
                piece.addAndGet(item.getCurrentQuantity());
                totalPiece.addAndGet(item.getMaxQuantity());
            }
        }
    }
}
