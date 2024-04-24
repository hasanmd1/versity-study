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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class CategoryController {

    private final HouseHoldService houseHoldService;
    private final CategoryService categoryService;
    private final ItemService itemService;
    private final AuthorizationService authorizationService;

    private static final String CATEGORY_ATTR = "category";
    private static final String HOUSEHOLD_ATTR = "household";
    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String MAIN_CATEGORY_ATTR = "mainCategory";
    private static final String AVAILABLE_SUB_CATEGORY_ATTR = "availableSubCategory";
    private static final String CLOSE_EXPIRATION_ATTR = "closeExpiration";
    private static final String FAR_EXPIRATION_ATTR = "farExpiration";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String CATEGORY_WITH_ID = "Category With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String OWNER_PERMISSION = "permission";
    private static final String REDIRECTION_HOUSEHOLD = "redirect:/household/";
    private static final String RETURN_CATEGORY_DETAILS = "category/categoryDetails";

    @GetMapping("/{householdId}/category/add")
    public String renderAddCategoryPage(@PathVariable Long householdId,
                                        Model model) {
        CategoryCreationDTO categoryCreationDTO = new CategoryCreationDTO();
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(CATEGORY_ATTR, categoryCreationDTO);
        return "category/addCategory";
    }

    @GetMapping("/{householdId}/category/{categoryId}")
    public String renderCategoryInfoPage(@PathVariable Long householdId,
                                         @PathVariable Long categoryId,
                                         Model model) {
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        List<Item> items = itemService.findItemsByCategory(category);

        List<Item> closeExpiration = items.stream().filter(item -> item.getExpiration() != null &&
                        item.getExpiration().after(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = items.stream().filter(item -> item.getExpiration() == null ||
                        item.getExpiration().before(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        AtomicDouble kilo = new AtomicDouble(0);
        AtomicDouble totalKilo = new AtomicDouble(0);
        AtomicDouble piece = new AtomicDouble(0);
        AtomicDouble totalPiece = new AtomicDouble(0);
        AtomicDouble liter = new AtomicDouble(0);
        AtomicDouble totalLiter = new AtomicDouble(0);
        categoryService.calculateQuantity(closeExpiration, kilo, totalKilo, piece, totalPiece, liter, totalLiter);
        categoryService.calculateQuantity(farExpiration, kilo, totalKilo, piece, totalPiece, liter, totalLiter);


        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("kilo", kilo);
        model.addAttribute("piece", piece);
        model.addAttribute("liter", liter);
        model.addAttribute("tkilo", totalKilo);
        model.addAttribute("tpiece", totalPiece);
        model.addAttribute("tliter", totalLiter);
        model.addAttribute(MAIN_CATEGORY_ATTR, category);
        model.addAttribute(AVAILABLE_SUB_CATEGORY_ATTR, categoryService.findAllSubCategory(category));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_CATEGORY_DETAILS;
    }

    @GetMapping("/{householdId}/category")
    public String renderCategoryPage(@PathVariable Long householdId, Model model) {
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("availableCategory", houseHold.getCategory());
        return "category/householdCategory";
    }

    @PostMapping("/{householdId}/category/add")
    public String addCategory(@PathVariable Long householdId,
                              @Valid @ModelAttribute("category") CategoryCreationDTO category,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute(CATEGORY_ATTR, category);
            return "category/addCategory";
        }

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        categoryService.addCategory(category, houseHold, null);

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("availableCategory", houseHold.getCategory());
        return "redirect:/household/{householdId}/category";
    }

    @GetMapping("/{householdId}/category/{categoryId}/delete")
    public RedirectView deleteCategory(@PathVariable Long categoryId,
                                       @PathVariable String householdId){

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        if (category.getMainCategory() == null ) {
            categoryService.deleteCategoryById(category.getId());
            return new RedirectView("/" + HOUSEHOLD_ATTR + "/" + householdId + "/" + CATEGORY_ATTR);
        }

        categoryService.deleteCategoryById(category.getId());

        return new RedirectView("/" + HOUSEHOLD_ATTR + "/" + householdId + "/" + CATEGORY_ATTR + "/" + category.getMainCategory().getId());
    }

    @GetMapping("/{householdId}/category/{categoryId}/return")
    public ModelAndView returnToMainCategory(@PathVariable Long householdId,
                                             @PathVariable Long categoryId,
                                             Model model) {


        Optional<Category> category = categoryService.findCategoryById(categoryId);

        if(!category.isPresent() || category.get().getMainCategory() == null) {
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR, (Map<String, ?>) model);
        }

        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR +"/" +
                category.get().getMainCategory().getId(), (Map<String, ?>) model);
    }

    @GetMapping("/{householdId}/category/{categoryId}/edit")
    public String renderEditingPage(@PathVariable Long householdId,
                                    @PathVariable Long categoryId,
                                    Model model) {

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        CategoryCreationDTO newCategory = new CategoryCreationDTO();

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(CATEGORY_ATTR, category);
        model.addAttribute("newCategory", newCategory);

        return "category/editCategory/categoryEdit";
    }

    @PostMapping("/{householdId}/category/{categoryId}/edit")
    public ModelAndView performEditing(@PathVariable Long householdId,
                                       @PathVariable Long categoryId,
                                       @ModelAttribute CategoryCreationDTO updatedCategory,
                                       Model model) {

        Household household = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category category = categoryService.updateCategory(categoryId, updatedCategory);

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(household));
        model.addAttribute(HOUSEHOLD_ATTR, household);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(CATEGORY_ATTR, category);

        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR + "/" + categoryId + "/return", (Map<String, ?>) model);
    }
}
