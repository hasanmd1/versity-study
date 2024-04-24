package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.item.ItemCreationDTO;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.datamodel.entity.item.ItemRelocationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.CategoryRepository;
import cz.cvut.fit.household.repository.ItemRepository;
import cz.cvut.fit.household.repository.LocationRepository;
import cz.cvut.fit.household.service.interfaces.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;


    private static final String ITEM_WITH_ID = "Item With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";

    @Override
    public Item addItem(ItemCreationDTO itemDto, Location location) {
        Item item = new Item(itemDto);
        item.setLocation(location);
        item.setCategory(itemDto.getCategory());
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(ItemCreationDTO updatedItem, Long id) {
        Item item = findItemById(id)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + id + DOES_NOT_EXIST));

        item.setTitle(updatedItem.getTitle());
        item.setDescription(updatedItem.getDescription());
        item.setExpiration(updatedItem.getExpiration());
        item.setQuantityType(updatedItem.getQuantityType());
        item.setMaxQuantity(Double.valueOf(updatedItem.getMaxQuantity()));
        item.setCurrentQuantity(Double.valueOf(updatedItem.getCurrentQuantity()));
        item.setCategory(updatedItem.getCategory());

        return itemRepository.save(item);
    }

    @Override
    public Item increaseItemQuantity(String increaseQuantityText, Long id) {
        Item item = findItemById(id)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + id + DOES_NOT_EXIST));

        double result = Double.parseDouble(increaseQuantityText) + item.getCurrentQuantity();

        if(result > item.getMaxQuantity())
            throw new IllegalArgumentException("Quantity should not be more than maximal quantity");
        item.setCurrentQuantity(result);

        return itemRepository.save(item);
    }

    @Override
    public Item decreaseItemQuantity(String decreaseQuantityText, Long id) {
        Item item = findItemById(id)
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + id + DOES_NOT_EXIST));

        double result = item.getCurrentQuantity() - Double.parseDouble(decreaseQuantityText);

        if(result < 0 || Double.parseDouble(decreaseQuantityText) < 0 )
            throw new IllegalArgumentException("Quantity should not be less than 0");
        item.setCurrentQuantity(result);

        return itemRepository.save(item);
    }

    @Override
    public Item relocateItem(ItemRelocationDTO itemRelocationDto) {
        Item item = findItemById(itemRelocationDto.getItemId())
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemRelocationDto.getItemId() + DOES_NOT_EXIST));
        Location location = locationRepository.findById(itemRelocationDto.getLocationId())
                .orElseThrow(() -> new NonExistentEntityException(ITEM_WITH_ID + itemRelocationDto.getLocationId() + DOES_NOT_EXIST));

        item.setLocation(location);

        return itemRepository.save(item);
    }

    @Override
    public List<Item> findItemsByLocation(Location location) {
        List<Item> items = location.getItems();
        List<Location> children = location.getSubLocations();

        for (Location childLocation : children) {
            items.addAll(findItemsByLocation(childLocation));
        }

        return sortItemList(items);
    }

    @Override
    public List<Item> findItemsByCategory(Category category) {
        List<Item> items = category.getItems();
        List<Category> children = category.getSubCategory();

        for (Category childCategory : children) {
            items.addAll(findItemsByCategory(childCategory));
        }

        return sortItemList(items);
    }

    public List<Item> sortItemList(List<Item>items){
        items.sort((i1, i2) -> {
            if (i1.getExpiration() != null && i2.getExpiration() == null) {
                return -1;
            } else if (i1.getExpiration() == null && i2.getExpiration() != null) {
                return 1;
            } else if (i1.getExpiration() == null && i2.getExpiration() == null) {
                return 0;
            }

            return i1.getExpiration().compareTo(i2.getExpiration());
        });

        return items;
    }

    @Override
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }
}
