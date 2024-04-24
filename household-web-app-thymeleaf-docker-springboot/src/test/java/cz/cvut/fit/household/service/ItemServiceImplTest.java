package cz.cvut.fit.household.service;


import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.item.ItemCreationDTO;
import cz.cvut.fit.household.datamodel.entity.item.ItemRelocationDTO;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.datamodel.enums.QuantityType;
import cz.cvut.fit.household.repository.CategoryRepository;
import cz.cvut.fit.household.repository.ItemRepository;
import cz.cvut.fit.household.repository.LocationRepository;
import cz.cvut.fit.household.service.interfaces.CategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    CategoryService categoryService;

    @Mock
    LocationRepository locationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    Category category1 = new Category(3L, household1, new ArrayList<>(), null, new ArrayList<>(), "newCategory", "nothing");
    List<Category> category = Collections.singletonList(category1);
    Category subcategory1 = new Category(4L, household1, new ArrayList<>(), category1, new ArrayList<>(), "newSubcategory", "nothing");
    List<Category> subcategory = Collections.singletonList(subcategory1);

    ItemCreationDTO itemDto1 = new ItemCreationDTO("item","that is item",subcategory1,QuantityType.KILOGRAM,"100","50",  Date.valueOf(LocalDate.now().plus(10, ChronoUnit.DAYS)));
    Location location1 = new Location(1L,null,new ArrayList<>(),null,new ArrayList<>(),"title","description");
    Item item = new Item(1L,subcategory1,location1,"item","that is item",QuantityType.KILOGRAM,100D,50D, Date.valueOf(LocalDate.now().plus(10, ChronoUnit.DAYS)));
    ItemRelocationDTO itemRelocationDto = new ItemRelocationDTO(item.getId(),location1.getId());

    @Before
    public void setup() {
        location1.setItems(Collections.singletonList(item));
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.ofNullable(item));
        when(locationRepository.findById(location1.getId())).thenReturn(java.util.Optional.ofNullable(location1));

    }

    @Test
    public void addItem() {
        itemService.addItem(itemDto1,location1);

        verify(itemRepository,times(1)).save(any(Item.class));

    }

    @Test
    public void updateItem() {
        Item updatedItem = new Item(item.getId(),category1,location1,"new_item","that is new_item",QuantityType.KILOGRAM,150D,20D, Date.valueOf(LocalDate.now().plus(10, ChronoUnit.DAYS)));
        ItemCreationDTO updatedItemCreationDTO = new ItemCreationDTO("new_item","that is new_item",category1,QuantityType.KILOGRAM,"150","20", Date.valueOf(LocalDate.now().plus(10, ChronoUnit.DAYS)));

        itemService.updateItem(updatedItemCreationDTO,item.getId());
        verify(itemRepository,times(1)).save(any(Item.class));
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(argument.capture());
        assertEquals(updatedItem.getTitle(), argument.getValue().getTitle());
        assertEquals(updatedItem.getId(), argument.getValue().getId());
        assertEquals(updatedItem.getCurrentQuantity(), argument.getValue().getCurrentQuantity());
        assertEquals(updatedItem.getMaxQuantity(), argument.getValue().getMaxQuantity());
        assertEquals(updatedItem.getExpiration(), argument.getValue().getExpiration());
        assertEquals(updatedItem.getCategory(), argument.getValue().getCategory());


    }

    @Test
    public void increaseQuantityOfItem() {
        itemService.increaseItemQuantity("20",item.getId());

        verify(itemRepository,times(1)).save(any(Item.class));
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(argument.capture());
        assertEquals(item.getCurrentQuantity(), argument.getValue().getCurrentQuantity());

    }

    @Test(expected = IllegalArgumentException.class)
    public void increaseQuantityOfItemInvalid() {
        itemService.increaseItemQuantity("120",item.getId());
    }


    @Test
    public void decreaseQuantityOfItem() {
        itemService.increaseItemQuantity("20",item.getId());

        verify(itemRepository,times(1)).save(any(Item.class));
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(argument.capture());
        assertEquals(item.getCurrentQuantity(), argument.getValue().getCurrentQuantity());

    }

    @Test(expected = IllegalArgumentException.class)
    public void decreaseQuantityOfItemInvalid() {
        itemService.decreaseItemQuantity("120",item.getId());
    }

    @Test
    public void findItemsByLocation() {
        List<Item> items = itemService.findItemsByLocation(location1);


        assertEquals(items.size(),location1.getItems().size());
        for (Item it: items) {
            assertEquals(it.getTitle(),item.getTitle());
        }
    }

    @Test
    public void relocateItem() {
        itemService.relocateItem(itemRelocationDto);

        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(argument.capture());
        assertEquals(item.getId(), argument.getValue().getId());
        assertEquals(item.getLocation().getId(), argument.getValue().getLocation().getId());
    }
}
