package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.item.ItemQuantityDTO;
import cz.cvut.fit.household.datamodel.entity.item.ItemRelocationDTO;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.events.OnInventoryChangeEvent;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.datamodel.entity.item.ItemCreationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.interfaces.CategoryService;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.ItemService;
import cz.cvut.fit.household.service.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemService itemService;
    private final LocationService locationService;
    private final HouseHoldService houseHoldService;
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;


    private static final String ITEM_DTO_ATTR = "itemDto";
    private static final String MAIN_LOCATION_ATTR = "mainLocation";
    private static final String CLOSE_EXPIRATION_ATTR = "closeExpiration";
    private static final String FAR_EXPIRATION_ATTR = "farExpiration";
    private static final String LOCATION_ATTR = "location";
    private static final String HOUSEHOLD_ATTR = "household";
    private static final String AVAILABLE_SUB_LOCATIONS_ATTR = "availableSubLocations";
    private static final String CATEGORY_ATTR = "category";
    private static final String ITEM_ATTR = "item";
    private static final String CATEGORIES_ATTR = "categories";
    private static final String ERROR_ATTR = "error";
    private static final String QUANTITY_ERROR_MSG = "Quantity shouldn't be greater that max quantity and less than zero";
    private static final String EXPIRATION_ATTR = "expiration";
    private static final String CURRENT_QUANTITY_ATTR = "currentQuantity";
    private static final String MAX_QUANTITY_ATTR = "maxQuantity";
    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String LOCATION_WITH_ID = "Location With id: ";
    private static final String ITEM_WITH_ID = "Item With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String ITEM_WITH_TITLE = "Item With title: ";
    private static final String CATEGORY_WITH_ID = "Category With id: ";
    private static final String REDIRECTION_HOUSEHOLD = "redirect:/household/";
    private static final String RETURN_QUANTITY_EDIT_CATEGORY = "items/edit/quantityEditFromCategory";
    private static final String RETURN_LOCATION_DETAIL = "locations/locationDetail";
    private static final String RETURN_ITEM_QUANTITY_EDIT = "items/edit/quantityEdit";
    private static final String RETURN_ADD_ITEM = "items/add-item";
    @GetMapping("/{householdId}/locations/{locationId}/items/add")
    public String renderAddItemPage(@PathVariable Long householdId,
                                    @PathVariable Long locationId,
                                    Model model) {
        ItemCreationDTO item = new ItemCreationDTO();

        List<Category> categories = categoryService.findwithHousehold(householdId);

        model.addAttribute(ITEM_ATTR, item);
        model.addAttribute(CATEGORIES_ATTR, categories);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("locationId", locationId);

        return RETURN_ADD_ITEM;
    }

    @PostMapping("/{householdId}/locations/{locationId}/items")
    public String addItem(@PathVariable Long householdId,
                          @PathVariable Long locationId,
                          @Valid @ModelAttribute("item") ItemCreationDTO item,
                          BindingResult result, Model model) {
        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));

        List<Category> categories = categoryService.findwithHousehold(householdId);

        try {
            double maxQuantity = Double.parseDouble(item.getMaxQuantity());
            double curQuantity = Double.parseDouble(item.getCurrentQuantity());

            if (item.getExpiration() != null && item.getExpiration().before(Date.valueOf(LocalDate.now()))) {
                result.rejectValue(EXPIRATION_ATTR, ERROR_ATTR, "Invalid expiration date");
            }

            if ((maxQuantity < curQuantity) || curQuantity < 0) {
                result.rejectValue(MAX_QUANTITY_ATTR, ERROR_ATTR, "Current quantity is higher that maximum");
            }

            if (!result.getAllErrors().isEmpty()) {
                model.addAttribute(CATEGORIES_ATTR, categories);
                return RETURN_ADD_ITEM;
            }
            if(item.getCategory() == null){
                setCategoryAndExpirationError(model, item);
                model.addAttribute(CATEGORIES_ATTR, categories);
                return RETURN_ADD_ITEM;
            }

            itemService.addItem(item, location);

            List<Item> closeExpiration = location.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                            i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                    )
                    .collect(Collectors.toList());
            List<Item> farExpiration = location.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                            i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                    .collect(Collectors.toList());

            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAIN_LOCATION_ATTR, location);
            model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(location));
            model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
            model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, ITEM_WITH_TITLE + item.getTitle() + " created. " + MAX_QUANTITY_ATTR + ": " + item.getMaxQuantity() + ", " + CURRENT_QUANTITY_ATTR + ": " + item.getCurrentQuantity() + ", " + EXPIRATION_ATTR + ": " + item.getExpiration()));
            return "redirect:/household/{householdId}/locations/{locationId}/";
        } catch (NumberFormatException e) {
            result.rejectValue(MAX_QUANTITY_ATTR, ERROR_ATTR, "Field should contain numeric value");
            result.rejectValue(CURRENT_QUANTITY_ATTR, ERROR_ATTR, "Field should contain numeric value");
            model.addAttribute(CATEGORIES_ATTR, categories);
            setCategoryAndExpirationError(model, item);

            return RETURN_ADD_ITEM;
        }
    }

    @GetMapping("/{householdId}/items")
    public String renderItemsList(@PathVariable Long householdId,
                                  Model model) {
        Household household = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        List<Item> items = new ArrayList<>();

        household.getLocations()
                .forEach(location -> items.addAll(
                        itemService.findItemsByLocation(location)));

        List<Item> closeExpiration = items.stream().filter(item -> item.getExpiration() != null &&
                        item.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = items.stream().filter(item -> item.getExpiration() == null ||
                        item.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return "items/ItemsView";
    }


    @GetMapping("/{householdId}/locations/{locationId}/items")
    public ModelAndView renderLocationItemsList(@PathVariable Long householdId,
                                                @PathVariable Long locationId,
                                                Model model) {

        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + LOCATION_ATTR + "s/" + locationId, (Map<String, ?>) model);
    }

    @GetMapping("/{householdId}/locations/{locationId}/items/{itemId}/delete")
    public ModelAndView deleteItem(@PathVariable Long householdId,
                                   @PathVariable Long locationId,
                                   @PathVariable Long itemId,
                                   Model model) {
        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));
        Item item = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));
        itemService.deleteItemById(itemId);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, ITEM_WITH_TITLE + item.getTitle() + " deleted"));
        model.addAttribute(LOCATION_ATTR, location);
        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + LOCATION_ATTR + "s/" + locationId, (Map<String, ?>) model);
    }

    @GetMapping("/{householdId}/category/{categoryId}/items/{itemId}/delete")
    public ModelAndView deleteItemFromCategory(@PathVariable Long householdId,
                                   @PathVariable Long categoryId,
                                   @PathVariable Long itemId,
                                   Model model) {
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));
        Item item = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));
        itemService.deleteItemById(itemId);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, ITEM_WITH_TITLE + item.getTitle() + " deleted"));
        model.addAttribute(CATEGORY_ATTR, category);
        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR + "/" + categoryId, (Map<String, ?>) model);
    }

    @GetMapping("/{householdId}/locations/{locationId}/items/{itemId}/edit")
    public String renderEditingPage(@PathVariable Long householdId,
                                    @PathVariable Long locationId,
                                    @PathVariable Long itemId,
                                    Model model) {
        setEditingPage(householdId, locationId, itemId, model);

        ItemCreationDTO newItem = new ItemCreationDTO();
        model.addAttribute("newItem", newItem);

        return "items/edit/itemEdit";
    }

    @PostMapping("/{householdId}/locations/{locationId}/items/{itemId}/edit")
    public String performEditing(@PathVariable Long householdId,
                                 @PathVariable Long locationId,
                                 @PathVariable Long itemId,
                                 @Valid @ModelAttribute("updatedItem") ItemCreationDTO updatedItem,
                                 Model model) {

        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));

        Item oldItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));
        String title = oldItem.getTitle();
        String prevCategory = oldItem.getCategory().getTitle();
        Double currentQuantity = oldItem.getCurrentQuantity();
        Double maxQuantity = oldItem.getMaxQuantity();
        java.util.Date expiration = oldItem.getExpiration();

        Item item = itemService.updateItem(updatedItem, itemId);

        List<Item> closeExpiration = location.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = location.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, ITEM_WITH_TITLE + oldItem.getTitle() + " updated. Before: title: " + title + ", category: " + prevCategory + ", " + MAX_QUANTITY_ATTR + maxQuantity + ", " + CURRENT_QUANTITY_ATTR + ": " + currentQuantity + ", " + EXPIRATION_ATTR + ": " + expiration + ". Now: title: " + item.getTitle() + ", " + MAX_QUANTITY_ATTR + item.getMaxQuantity() + ", " + CURRENT_QUANTITY_ATTR + ": " + item.getCurrentQuantity() + ", " + EXPIRATION_ATTR + ": " + item.getExpiration()));

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, location);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(location));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);
        model.addAttribute(ITEM_ATTR, item);
        return "redirect:/household/{householdId}/locations/{locationId}";

    }

    @GetMapping("/{householdId}/category/{categoryId}/items/{itemId}/edit")
    public String renderEditingPageFromCategory(@PathVariable Long householdId,
                                                @PathVariable Long categoryId,
                                                @PathVariable Long itemId,
                                                Model model) {
        setEditingPageFromCategory(householdId, categoryId, itemId, model);

        ItemCreationDTO newItem = new ItemCreationDTO();
        model.addAttribute("newItem", newItem);

        return "items/edit/itemEditFromCategory";
    }

    @PostMapping("/{householdId}/category/{categoryId}/items/{itemId}/edit")
    public String performEditingFromCategory(@PathVariable Long householdId,
                                             @PathVariable Long categoryId,
                                             @PathVariable Long itemId,
                                             @ModelAttribute ItemCreationDTO updatedItem,
                                             Model model) {

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));
        Item oldItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));
        Item item = itemService.updateItem(updatedItem, itemId);

        List<Item> closeExpiration = category.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = category.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());
        String title = oldItem.getTitle();
        String prevCategory = oldItem.getCategory().getTitle();
        Double currentQuantity = oldItem.getCurrentQuantity();
        Double maxQuantity = oldItem.getMaxQuantity();
        java.util.Date expiration = oldItem.getExpiration();

        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("mainCategory", category);
        model.addAttribute("availableSubCategories", categoryService.findAllSubCategory(category));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, ITEM_WITH_TITLE + oldItem.getTitle() + " updated. Before: title: " + title + ", category:" +  prevCategory + ", " + MAX_QUANTITY_ATTR + maxQuantity + ", " + CURRENT_QUANTITY_ATTR + ": " + currentQuantity + ", " + EXPIRATION_ATTR + ": " + expiration + ". Now: title: " + item.getTitle() + ", " + MAX_QUANTITY_ATTR + item.getMaxQuantity() + ", " + CURRENT_QUANTITY_ATTR + ": " + item.getCurrentQuantity() + ", " + EXPIRATION_ATTR + ": " + item.getExpiration()));
        return "redirect:/household/{householdId}/category/{categoryId}";

    }

    @GetMapping("/{householdId}/locations/{locationId}/items/{itemId}/change-quantity")
    public String renderEditQuantityPage(@PathVariable Long householdId,
                                         @PathVariable Long locationId,
                                         @PathVariable Long itemId,
                                         Model model) {
        setEditingPage(householdId, locationId, itemId, model);

        ItemQuantityDTO itemDto = new ItemQuantityDTO();
        model.addAttribute(ITEM_DTO_ATTR,itemDto);

        return RETURN_ITEM_QUANTITY_EDIT;
    }

    @PostMapping(value = "/{householdId}/locations/{locationId}/items/{itemId}/increase-quantity")
    public ModelAndView increaseQuantity(@PathVariable Long householdId,
                                         @PathVariable Long locationId,
                                         @PathVariable Long itemId,
                                         @Valid @ModelAttribute("itemDto") ItemQuantityDTO itemDto,
                                         BindingResult result,
                                         Model model) {
        setEditingPage(householdId, locationId, itemId, model);

        if (result.hasErrors()) {
            return new ModelAndView(RETURN_ITEM_QUANTITY_EDIT, (Map<String, ?>) model);
        }

        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));

        Item foundItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        try {
            Double changedAmount = Double.valueOf(itemDto.getCurrentQuantity());
            Item item = itemService.increaseItemQuantity(itemDto.getCurrentQuantity(), itemId);

            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(LOCATION_ATTR, location);
            model.addAttribute(ITEM_ATTR, item);

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "Quantity of item with title:- " + foundItem.getTitle() + " was increased. Was: " + Double.valueOf(foundItem.getCurrentQuantity() - changedAmount) + ", now:- " + item.getCurrentQuantity()));
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" +LOCATION_ATTR + "s/" + locationId + "/items", (Map<String, ?>) model);
        } catch (RuntimeException e) {
            result.rejectValue(CURRENT_QUANTITY_ATTR, ERROR_ATTR,  QUANTITY_ERROR_MSG);
            return new ModelAndView(RETURN_ITEM_QUANTITY_EDIT, (Map<String, ?>) model);
        }
    }

    @PostMapping(value = "/{householdId}/locations/{locationId}/items/{itemId}/decrease-quantity")
    public ModelAndView decreaseQuantity(@PathVariable Long householdId,
                                         @PathVariable Long locationId,
                                         @PathVariable Long itemId,
                                         @Valid @ModelAttribute("itemDto") ItemQuantityDTO itemDto,
                                         BindingResult result,
                                         Model model) {

        setEditingPage(householdId, locationId, itemId, model);

        if (result.hasErrors()) {
            return new ModelAndView(RETURN_ITEM_QUANTITY_EDIT, (Map<String, ?>) model);
        }

        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));

        Item foundItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        try {
            Double changedAmount = Double.valueOf(itemDto.getCurrentQuantity());
            Item item = itemService.decreaseItemQuantity(itemDto.getCurrentQuantity(), itemId);

            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(LOCATION_ATTR, location);
            model.addAttribute(ITEM_ATTR, item);


            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "Quantity of item with title:- " + foundItem.getTitle() + " was decreased. Was: " + Double.valueOf(foundItem.getCurrentQuantity() + changedAmount) + ", now:- " + item.getCurrentQuantity()));
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + LOCATION_ATTR + "s/" + locationId + "/items", (Map<String, ?>) model);
        } catch (RuntimeException e) {
            result.rejectValue(CURRENT_QUANTITY_ATTR, ERROR_ATTR,  QUANTITY_ERROR_MSG);
            return new ModelAndView(RETURN_ITEM_QUANTITY_EDIT, (Map<String, ?>) model);
        }
    }

    @GetMapping("/{householdId}/category/{categoryId}/items/{itemId}/change-quantity")
    public String renderEditQuantityPageFromCategory(@PathVariable Long householdId,
                                                     @PathVariable Long categoryId,
                                                     @PathVariable Long itemId,
                                                     Model model) {
        setEditingPageFromCategory(householdId, categoryId, itemId, model);

        ItemQuantityDTO itemDto = new ItemQuantityDTO();
        model.addAttribute(ITEM_DTO_ATTR,itemDto);

        return RETURN_QUANTITY_EDIT_CATEGORY;
    }

    @PostMapping(value = "/{householdId}/category/{categoryId}/items/{itemId}/increase-quantity")
    public ModelAndView increaseQuantityFromCategory(@PathVariable Long householdId,
                                                     @PathVariable Long categoryId,
                                                     @PathVariable Long itemId,
                                                     @Valid @ModelAttribute("itemDto") ItemQuantityDTO itemDto,
                                                     BindingResult result,
                                                     Model model) {
        setEditingPageFromCategory(householdId, categoryId, itemId, model);

        if (result.hasErrors()) {
            return new ModelAndView(RETURN_QUANTITY_EDIT_CATEGORY, (Map<String, ?>) model);
        }

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        Item foundItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        try {
            Double changedAmount = Double.valueOf(itemDto.getCurrentQuantity());
            Item item = itemService.increaseItemQuantity(itemDto.getCurrentQuantity(), itemId);

            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(CATEGORY_ATTR, category);
            model.addAttribute(ITEM_ATTR, item);

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "Quantity of item with title: " + foundItem.getTitle() + " was increased. Was: " + Double.valueOf(foundItem.getCurrentQuantity() - changedAmount) + ", now: " + item.getCurrentQuantity()));
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR + "/" + categoryId, (Map<String, ?>) model);
        } catch (RuntimeException e) {
            result.rejectValue(CURRENT_QUANTITY_ATTR, ERROR_ATTR,  QUANTITY_ERROR_MSG);
            return new ModelAndView(RETURN_QUANTITY_EDIT_CATEGORY, (Map<String, ?>) model);
        }
    }

    @PostMapping(value = "/{householdId}/category/{categoryId}/items/{itemId}/decrease-quantity")
    public ModelAndView decreaseQuantityFromCategory(@PathVariable Long householdId,
                                                     @PathVariable Long categoryId,
                                                     @PathVariable Long itemId,
                                                     @Valid @ModelAttribute("itemDto") ItemQuantityDTO itemDto,
                                                     BindingResult result,
                                                     Model model) {

        setEditingPageFromCategory(householdId, categoryId, itemId, model);
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));


        if (result.hasErrors()) {
            return new ModelAndView(RETURN_QUANTITY_EDIT_CATEGORY, (Map<String, ?>) model);
        }

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        Item foundItem = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        try {
            Double changedAmount = Double.valueOf(itemDto.getCurrentQuantity());
            Item item = itemService.decreaseItemQuantity(itemDto.getCurrentQuantity(), itemId);

            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(CATEGORY_ATTR, category);
            model.addAttribute(ITEM_ATTR, item);

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "Quantity of item with title: " + foundItem.getTitle() + " was decreased. Was: " + Double.valueOf(foundItem.getCurrentQuantity() + changedAmount) + ", now: " + item.getCurrentQuantity()));
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + CATEGORY_ATTR + "/" + categoryId, (Map<String, ?>) model);
        } catch (RuntimeException e) {
            result.rejectValue(CURRENT_QUANTITY_ATTR, ERROR_ATTR,  QUANTITY_ERROR_MSG);
            return new ModelAndView(RETURN_QUANTITY_EDIT_CATEGORY, (Map<String, ?>) model);
        }
    }

    @GetMapping("/{householdId}/locations/{locationId}/items/{itemId}/relocate")
    public String itemRelocationPageRender(@PathVariable Long householdId,
                                           @PathVariable Long locationId,
                                           @PathVariable Long itemId,
                                           Model model){
        List<Location> availableLocations = locationService.findLocationsInHousehold(householdId);
        ItemRelocationDTO itemRelocationDto = new ItemRelocationDTO(itemId);

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("locationId", locationId);
        model.addAttribute("locations", availableLocations);
        model.addAttribute(ITEM_DTO_ATTR, itemRelocationDto);


        return "items/edit/relocationItem";
    }

    @PostMapping("/{householdId}/locations/{locationId}/items/{itemId}/relocate")
    public String relocateItem(@PathVariable Long householdId,
                               @PathVariable Long locationId,
                               @PathVariable Long itemId,
                               @ModelAttribute ItemRelocationDTO itemDto,
                               Model model){
        Item item = itemService.relocateItem(itemDto);
        Location oldLocation = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));
        List<Item> closeExpiration = oldLocation.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = oldLocation.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(ITEM_ATTR, item);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, oldLocation);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, oldLocation.getSubLocations());
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);
        return "redirect:/household/{householdId}/locations/{locationId}";
    }

    @GetMapping("/{householdId}/category/{categoryId}/items/{itemId}/relocate")
    public String itemRelocationPageRenderFromCategory(@PathVariable Long householdId,
                                                       @PathVariable Long categoryId,
                                                       @PathVariable Long itemId,
                                                       Model model){
        List<Location> availableLocations = locationService.findLocationsInHousehold(householdId);
        List<Category> availableCategories = categoryService.findwithHousehold(householdId);

        ItemRelocationDTO itemRelocationDto = new ItemRelocationDTO(itemId);

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("locations", availableLocations);
        model.addAttribute(CATEGORIES_ATTR, availableCategories);
        model.addAttribute(ITEM_DTO_ATTR, itemRelocationDto);


        return "items/edit/relocationItemFromCategory";
    }

    @PostMapping("/{householdId}/category/{categoryId}/items/{itemId}/relocate")
    public String relocateItemFromCategory(@PathVariable Long householdId,
                               @PathVariable Long categoryId,
                               @PathVariable Long itemId,
                               @ModelAttribute ItemRelocationDTO itemDto,
                               Model model){
        Item item = itemService.relocateItem(itemDto);
        Location oldLocation = locationService.findLocationById(item.getLocation().getId())
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + item.getLocation().getId() + DOES_NOT_EXIST));
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));


        Category oldCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));

        List<Item> closeExpiration = oldCategory.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = oldCategory.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute("oldLocation", oldLocation);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute("mainCategory", oldCategory);
        model.addAttribute("availableSubCategories", oldCategory.getSubCategory());
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);
        return "redirect:/household/{householdId}/category/{categoryId}";
    }

    private void setEditingPage(Long householdId,
                                Long locationId,
                                Long itemId,
                                Model model) {
        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(LOCATION_WITH_ID + locationId + DOES_NOT_EXIST));
        Item item = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        List<Category> categories = categoryService.findwithHousehold(householdId);
        model.addAttribute(CATEGORIES_ATTR, categories);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(LOCATION_ATTR, location);
        model.addAttribute(ITEM_ATTR, item);
    }

    private void setEditingPageFromCategory(Long householdId,
                                Long categoryId,
                                Long itemId,
                                Model model) {
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NonExistentEntityException(CATEGORY_WITH_ID + categoryId + DOES_NOT_EXIST));
        Item item = itemService.findItemById(itemId)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemId + DOES_NOT_EXIST));

        List<Category> categories = categoryService.findwithHousehold(householdId);
        model.addAttribute(CATEGORIES_ATTR, categories);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(CATEGORY_ATTR, category);
        model.addAttribute(ITEM_ATTR, item);
    }

    private void setCategoryAndExpirationError(Model model, ItemCreationDTO item){
        if(Objects.isNull(item.getCategory())){
            model.addAttribute("categoryError", "Category cannot be empty");

        }
    }

}
