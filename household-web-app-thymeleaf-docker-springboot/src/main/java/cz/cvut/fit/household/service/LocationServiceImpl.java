package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.location.LocationCreationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.LocationRepository;
import cz.cvut.fit.household.service.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location addLocation(LocationCreationDTO updatedLocation, Household household, Location mainLocation) {
        Location location = new Location();

        location.setMainLocation(mainLocation);
        location.setHouseHold(household);
        location.setTitle(updatedLocation.getTitle());
        location.setDescription(updatedLocation.getDescription());

        return locationRepository.save(location);
    }

    @Override
    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public  List<Location>findLocationsInHousehold(Long householdId){
        List<Location>locations = locationRepository.findAll();
        Boolean checker;
        List<Location>locationList = new ArrayList<>();
        for(Location location :locations){
            if(location.getHouseHold() == null){
                checker = findLocationWithHousehold(location, householdId);
                if(checker.equals(true)){
                    locationList.add(location);
                }
            }
            else if(location.getHouseHold().getId().equals(householdId)) {
                locationList.add(location);
            }
        }
        return locationList;
    }

    @Override
    public List<Location> findAllSubLocations(Location mainLocation) {
        List<Location> allSubLocations = mainLocation.getSubLocations();
        List<Location> resultList = new ArrayList<>();

        for(Location subLocation : allSubLocations){
            resultList.add(subLocation);
            resultList.addAll(findAllSubLocations(subLocation));
        }

        return resultList;
    }

    @Override
    public Optional<Location> findLocationById(Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public void deleteLocationById(Long id) {
        locationRepository.deleteById(id);
    }

    @Override
    public Location updateLocation(Long locationId, LocationCreationDTO updatedLocation) {
        Location location = findLocationById(locationId)
                .orElseThrow(() -> new NonExistentEntityException("Location with id: " + locationId + " doesn't exist"));

        location.setTitle(updatedLocation.getTitle());
        location.setDescription(updatedLocation.getDescription());

        return locationRepository.save(location);
    }

    private boolean findLocationWithHousehold(Location location, Long householdId) {
        while(location.getHouseHold() == null){
            location = location.getMainLocation();
        }
        return location.getHouseHold().getId().equals(householdId);
    }
}
