package cz.cvut.fit.household.service.interfaces;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.location.LocationCreationDTO;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    Location addLocation(LocationCreationDTO location , Household household, Location mainLocation);

    List<Location> findAllLocations();

    List<Location> findAllSubLocations(Location location);

    Optional<Location> findLocationById(Long id);

    void deleteLocationById(Long id);

    Location updateLocation(Long locationId, LocationCreationDTO updatedLocation);

    List<Location>findLocationsInHousehold(Long householdId);
}
