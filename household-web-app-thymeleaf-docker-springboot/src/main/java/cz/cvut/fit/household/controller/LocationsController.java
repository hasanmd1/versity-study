package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.item.Item;
import cz.cvut.fit.household.datamodel.entity.location.LocationCreationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.ItemService;
import cz.cvut.fit.household.service.interfaces.LocationService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class LocationsController {

    private final HouseHoldService houseHoldService;
    private final LocationService locationService;
    private final ItemService itemService;

    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String LOCATION_ATTR = "location";
    private static final String REDIRECTION_HOUSEHOLD = "redirect:/household/";
    private static final String MAIN_LOCATION_ATTR = "mainLocation";
    private static final String AVAILABLE_SUB_LOCATIONS_ATTR = "availableSubLocations";
    private static final String CLOSE_EXPIRATION_ATTR = "closeExpiration";
    private static final String FAR_EXPIRATION_ATTR = "farExpiration";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String RETURN_LOCATION_DETAILS = "locations/locationDetail";


    @GetMapping("/{householdId}/locations/add")
    public String renderAddLocationPage(@PathVariable Long householdId,
                                        Model model) {
        LocationCreationDTO location = new LocationCreationDTO();
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(LOCATION_ATTR, location);
        return "locations/addLocation";
    }

    @GetMapping("/{householdId}/locations/{locationId}")
    public String renderLocationInfoPage(@PathVariable Long householdId,
                                         @PathVariable Long locationId,
                                         Model model) {
        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException("Location with id: " + locationId + DOES_NOT_EXIST));
        List<Item> items = itemService.findItemsByLocation(location);
        List<Item> closeExpiration = items.stream().filter(item -> item.getExpiration() != null &&
                        item.getExpiration().after(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant()))
                )
                .collect(Collectors.toList());
        List<Item> farExpiration = items.stream().filter(item -> item.getExpiration() == null ||
                        item.getExpiration().before(Date.from(LocalDateTime.now().minusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
                .collect(Collectors.toList());

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAIN_LOCATION_ATTR, location);
        model.addAttribute(AVAILABLE_SUB_LOCATIONS_ATTR, locationService.findAllSubLocations(location));
        model.addAttribute(CLOSE_EXPIRATION_ATTR, closeExpiration);
        model.addAttribute(FAR_EXPIRATION_ATTR, farExpiration);

        return RETURN_LOCATION_DETAILS;
    }

    @GetMapping("/{householdId}/locations")
    public String renderLocationsPage(@PathVariable Long householdId, Model model) {
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("availableLocations", houseHold.getLocations());
        return "locations/householdLocations";
    }

    @PostMapping("/{householdId}/locations/add")
    public String addLocation(@PathVariable Long householdId,
                              @Valid @ModelAttribute("location") LocationCreationDTO location,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            return "locations/addLocation";
        }

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        locationService.addLocation(location, houseHold, null);

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute("availableLocations", houseHold.getLocations());
        return "redirect:/household/{householdId}/locations";
    }

    @GetMapping("/{householdId}/locations/{locationId}/delete")
    public RedirectView deleteLocation(@PathVariable Long locationId,
                                       @PathVariable String householdId){

        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        if (location.getMainLocation() == null ) {
            locationService.deleteLocationById(location.getId());
            return new RedirectView("/household/" + householdId + "/locations");
        }

        locationService.deleteLocationById(location.getId());

        return new RedirectView("/household/" + householdId + "/" + LOCATION_ATTR +"s/" + location.getMainLocation().getId());
    }

    @GetMapping("/{householdId}/locations/{locationId}/return")
    public ModelAndView returnToMainLocation(@PathVariable Long householdId,
                                             @PathVariable Long locationId,
                                             Model model) {


        Optional<Location> location = locationService.findLocationById(locationId);

        if(!location.isPresent() || location.get().getMainLocation() == null) {
            return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/locations", (Map<String, ?>) model);
        }

        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/locations/" +
                location.get().getMainLocation().getId(), (Map<String, ?>) model);
    }

    @GetMapping("/{householdId}/locations/{locationId}/edit")
    public String renderEditingPage(@PathVariable Long householdId,
                                    @PathVariable Long locationId,
                                    Model model) {

        Location location = locationService.findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        LocationCreationDTO newLocation = new LocationCreationDTO();

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(LOCATION_ATTR, location);
        model.addAttribute("newLocation", newLocation);

        return "locations/edit/locationEdit";
    }

    @PostMapping("/{householdId}/locations/{locationId}/edit")
    public ModelAndView performEditing(@PathVariable Long householdId,
                                       @PathVariable Long locationId,
                                       @ModelAttribute LocationCreationDTO updatedLocation,
                                       Model model) {

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Location location = locationService.updateLocation(locationId, updatedLocation);

        model.addAttribute("household", houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(LOCATION_ATTR, location);

        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/locations/" + locationId + "/return", (Map<String, ?>) model);
    }

}
