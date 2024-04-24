package cz.cvut.fit.household.controller;

import com.google.common.util.concurrent.AtomicDouble;
import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.category.CategoryCreationDTO;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.AuthorizationService;
import cz.cvut.fit.household.service.interfaces.CategoryService;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class SubCategoryController {
    private final CategoryService categoryService;
    private final ItemService itemService;
    private final HouseHoldService houseHoldService;
    private final AuthorizationService authorizationService;

    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String MAIN_CATEGORY_ATTR = "mainCategory";
    private static final String AVAILABLE_SUB_CATEGORY_ATTR = "availableSubCategory";
    private static final String CLOSE_EXPIRATION_ATTR = "closeExpiration";
    private static final String FAR_EXPIRATION_ATTR = "farExpiration";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String CATEGORY_WITH_ID = "Category With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String OWNER_PERMISSION = "permission";
    private static final String RETURN_CATEGORY_DETAILS = "category/categoryDetails";

    @PostMapping("/{householdId}/category/{categoryId}/subcategory/add")
    public String addSubcategory(@PathVariable Long householdId,
                                 @PathVariable Long categoryId,
                                 @Valid @ModelAttribute("subCategory") CategoryCreationDTO subCategory,
                                 BindingResult result, Model model) {
        if(result.hasErrors()) {
            return "category/subCategory/addSubcategory";
        }

        Household household = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category mainCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        categoryService.addCategory(subCategory, null, mainCategory);

        List<Item> items = itemService.findItemsByCategory(mainCategory);
        AtomicDouble kilo = new AtomicDouble(0);
        AtomicDouble totalKilo = new AtomicDouble(0);
        AtomicDouble piece = new AtomicDouble(0);
        AtomicDouble totalPiece = new AtomicDouble(0);
        AtomicDouble liter = new AtomicDouble(0);
        AtomicDouble totalLiter = new AtomicDouble(0);

        List<Item> closeExpiration = items.stream().filter(item -> item.getExpiration() != null &&
                        item.getExpiration().after(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = items.stream().filter(item -> item.getExpiration() == null ||
                        item.getExpiration().before(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        categoryService.calculateQuantity(closeExpiration, kilo, totalKilo, piece, totalPiece, liter, totalLiter);
        categoryService.calculateQuantity(farExpiration, kilo, totalKilo, piece, totalPiece, liter, totalLiter);


        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(household));
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("kilo", kilo);
        model.addAttribute("piece", piece);
        model.addAttribute("liter", liter);
        model.addAttribute("tkilo", totalKilo);
        model.addAttribute("tpiece", totalPiece);
        model.addAttribute("tliter", totalLiter);
        model.addAttribute(MAIN_CATEGORY_ATTR, mainCategory);
        model.addAttribute(AVAILABLE_SUB_CATEGORY_ATTR, categoryService.findAllSubCategory(mainCategory));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return "redirect:/household/{householdId}/category/{categoryId}";
    }

    @GetMapping("/{householdId}/category/{categoryId}/subcategory/view")
    public String getSubcategoryView(@PathVariable Long householdId,
                                     @PathVariable Long categoryId,
                                     Model model) {

        Household household = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category mainCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        List<Item> closeExpiration = mainCategory.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = mainCategory.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(household));
        model.addAttribute("household", household);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_CATEGORY_ATTR, mainCategory);
        model.addAttribute(AVAILABLE_SUB_CATEGORY_ATTR, categoryService.findAllSubCategory(mainCategory));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_CATEGORY_DETAILS;
    }

    @GetMapping("/{householdId}/category/{categoryId}/subcategory/add")
    public String renderAddSubcategoryPage(@PathVariable Long householdId,
                                           @PathVariable Long categoryId,
                                           Model model) {


        Category mainCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        CategoryCreationDTO category = new CategoryCreationDTO();
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("mainCategoryId", categoryId);
        model.addAttribute("category", category);
        model.addAttribute(MAIN_CATEGORY_ATTR, mainCategory);
        return "category/subCategory/addSubcategory";
    }

    @GetMapping("/{householdId}/category/{categoryId}/subcategory/{subcategoryId}/delete")
    public String deleteSubcategory(@PathVariable Long householdId,
                                    @PathVariable Long categoryId,
                                    @PathVariable Long subcategoryId,
                                    Model model) {


        Household household = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category mainCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        List<Item> closeExpiration = mainCategory.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = mainCategory.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        categoryService.deleteCategoryById(subcategoryId);



        model.addAttribute("household", household);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(AVAILABLE_SUB_CATEGORY_ATTR, categoryService.findAllSubCategory(mainCategory));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_CATEGORY_DETAILS;
    }
}
