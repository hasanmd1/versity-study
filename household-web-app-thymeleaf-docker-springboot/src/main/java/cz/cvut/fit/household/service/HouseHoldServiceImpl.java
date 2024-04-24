package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.household.HouseholdCreationDTO;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.household.jpa.HouseHoldRepository;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class HouseHoldServiceImpl implements HouseHoldService {

    private final HouseHoldRepository houseHoldRepository;

    @Autowired
    public HouseHoldServiceImpl(HouseHoldRepository houseHoldRepository) {
        this.houseHoldRepository = houseHoldRepository;
    }

    @Override
    public Household createHousehold(HouseholdCreationDTO updatedHouseHold) {
        Household household = new Household();

        household.setTitle(updatedHouseHold.getTitle());
        household.setDescription(updatedHouseHold.getDescription());
        return houseHoldRepository.save(household);
    }

    @Override
    public Household updateHousehold(HouseholdCreationDTO newHouseHold, Long id) {
        Household household = findHouseHoldById(id)
                .orElseThrow(() -> new NonExistentEntityException("Not exist household with id: " + id));

        household.setTitle(newHouseHold.getTitle());
        household.setDescription(newHouseHold.getDescription());

        return houseHoldRepository.save(household);
    }

    @Override
    public List<Household> findAllHouseholds() {
        return houseHoldRepository.findAll();
    }

    @Override
    public List<Household> findHouseholdsByUsername(String username) {
        List<Household> households = houseHoldRepository.findAll();
        List<Household> resultHouseholds = new ArrayList<>();

        for (Household houseHold : households) {
            for (Membership membership : houseHold.getMemberships()) {
                if (membership.getUser().getUsername().equals(username)) {
                    resultHouseholds.add(houseHold);
                }
            }
        }

        return resultHouseholds;
    }

    @Override
    public Optional<Household> findHouseHoldById(Long id) {
        return houseHoldRepository.findById(id);
    }

    @Override
    public List<Membership> findMembershipsByHouseholdId(Long id) {
        Optional<Household>householdOptional =   houseHoldRepository.findById(id);
        if(householdOptional.isPresent()){
            return householdOptional.get().getMemberships();
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteHouseholdById(Long id) {
        houseHoldRepository.deleteById(id);
    }
}
