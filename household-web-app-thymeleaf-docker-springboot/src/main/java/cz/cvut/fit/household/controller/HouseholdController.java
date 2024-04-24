package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.household.HouseholdCreationDTO;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.MembershipService;
import cz.cvut.fit.household.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller which manages all requests related to households.
 */
@Controller
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseHoldService householdService;
    private final UserService userService;
    private final MembershipService membershipService;

    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String HOUSEHOLD_ATTR = "household";

    @GetMapping("/household/{householdId}")
    public String renderHouseholdMainPage(@PathVariable Long householdId, Model model) {
        Household household = householdService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException("Household with id: " + householdId + " doesn't exist"));

        model.addAttribute(HOUSEHOLD_ATTR, household);
        return "household/householdMain";
    }

    @GetMapping("/households/add")
    public String renderCreateHouseholdPage(Model model) {
        model.addAttribute("houseHold", new Household());
        return "household/addHousehold";
    }
    @PostMapping("/households/add")
    public String createHousehold(@Valid @ModelAttribute("houseHold") HouseholdCreationDTO household, BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            return "household/addHousehold";
        }
        Household houseHold = householdService.createHousehold(household);

        User user = userService.findUserByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user no longer exists in the database"));
        Membership membership = new Membership();
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setMembershipRole(MembershipRole.OWNER);

        membershipService.createMembership(membership, user, houseHold);


        List<Membership> pendingMemberships =  user.getMemberships()
                .stream().filter(mem -> mem.getStatus().equals(MembershipStatus.PENDING))
                .collect(Collectors.toList());

        List<Membership> activeMemberships =  user.getMemberships()
                .stream().filter(mem -> mem.getStatus().equals(MembershipStatus.ACTIVE))
                .collect(Collectors.toList());

        model.addAttribute("pendingHouseholds", pendingMemberships);
        model.addAttribute("activeHouseholds", activeMemberships);
        return "redirect:/welcome";
    }

    @GetMapping("/household/{householdId}/edit")
    public String renderHouseholdEditPage(@PathVariable Long householdId,
                                          Model model) {
        Household household = householdService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException("Household with id: " + householdId + " doesn't exist"));

        HouseholdCreationDTO houseHold = new HouseholdCreationDTO(household.getTitle(), household.getDescription());

        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        return "household/edit/householdEdit";
    }

    @PostMapping("/household/{householdId}/edit")
    public String editHousehold(@PathVariable Long householdId,
                                @Valid @ModelAttribute HouseholdCreationDTO household,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute(HOUSEHOLD_ATTR, household);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            return "household/edit/householdEdit";
        }

        Household updatedHousehold = householdService.updateHousehold(household, householdId);

        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(HOUSEHOLD_ATTR, updatedHousehold);
        return "redirect:/household/{householdId}";
    }
}
