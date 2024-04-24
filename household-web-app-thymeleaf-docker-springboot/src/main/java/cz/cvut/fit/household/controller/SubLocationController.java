package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.datamodel.entity.location.LocationCreationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.interfaces.ItemService;
import cz.cvut.fit.household.service.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class SubLocationController {

    private final LocationService locationService;
    private final ItemService itemService;

    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String MAIN_LOCATION_ATTR = "mainLocation";
    private static final String AVAILABLE_SUB_LOCATIONS_ATTR = "availableSubLocations";
    private static final String CLOSE_EXPIRATION_ATTR = "closeExpiration";
    private static final String FAR_EXPIRATION_ATTR = "farExpiration";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String RETURN_LOCATION_DETAILS = "locations/locationDetail";

    @PostMapping("/{householdId}/locations/{locationId}/sublocations/add")
    public String addSubLocation(@PathVariable Long householdId,
                                 @PathVariable Long locationId,
                                 @Valid @ModelAttribute("subLocation") LocationCreationDTO subLocation,
                                 BindingResult result, Model model) {

        if(result.hasErrors()) {
            return  "locations/sub/addSubLocation";
        }

        Location mainLocation = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        locationService.addLocation(subLocation, null, mainLocation);

        List<Item> closeExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, mainLocation);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(mainLocation));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return "redirect:/household/{householdId}/locations/{locationId}";
    }

    @GetMapping("/{householdId}/locations/{locationId}/sublocations/view")
    public String getSubLocationView(@PathVariable Long householdId,
                                     @PathVariable Long locationId,
                                     Model model) {

        Location mainLocation = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        List<Item> closeExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, mainLocation);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(mainLocation));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_LOCATION_DETAILS;
    }

    @GetMapping("/{householdId}/locations/{locationId}/sublocations/add")
    public String renderAddSubLocationPage(@PathVariable Long householdId,
                                           @PathVariable Long locationId,
                                           Model model) {


        Location mainLocation = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException("Location with id: " + locationId + DOES_NOT_EXIST));

        LocationCreationDTO location = new LocationCreationDTO();

        model.addAttribute(MAIN_LOCATION_ATTR, mainLocation);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("mainLocationId", locationId);
        model.addAttribute("location", location);
        return "locations/sub/addSubLocation";
    }

    @GetMapping("/{householdId}/locations/{locationId}/sublocations/{sublocationId}/delete")
    public String deleteSubLocation(@PathVariable Long householdId,
                                    @PathVariable Long locationId,
                                    @PathVariable Long sublocationId,
                                    Model model) {


        Location mainLocation = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        List<Item> closeExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() != null &&
                        i1.getExpiration().after(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = mainLocation.getItems().stream().filter(i1 -> i1.getExpiration() == null ||
                        i1.getExpiration().before(java.util.Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        locationService.deleteLocationById(sublocationId);



        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, mainLocation);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(mainLocation));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_LOCATION_DETAILS;
    }
}
