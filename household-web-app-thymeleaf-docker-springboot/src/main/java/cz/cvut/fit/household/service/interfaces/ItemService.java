package cz.cvut.fit.household.service.interfaces;

import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.item.ItemCreationDTO;
import cz.cvut.fit.household.datamodel.entity.item.ItemRelocationDTO;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item addItem(ItemCreationDTO item, Location location);

    Item updateItem(ItemCreationDTO updatedItem, Long id);

    Item increaseItemQuantity(String increaseQuantity, Long id);

    Item decreaseItemQuantity(String  decreaseQuantity, Long id);

    Item relocateItem(ItemRelocationDTO itemRelocationDto);

    List<Item> findItemsByLocation(Location location);

    List<Item> findItemsByCategory(Category category);

    List<Item> findItems();

    Optional<Item> findItemById(Long id);

    void deleteItemById(Long id);

    public List<Item> sortItemList(List<Item>items);
}
