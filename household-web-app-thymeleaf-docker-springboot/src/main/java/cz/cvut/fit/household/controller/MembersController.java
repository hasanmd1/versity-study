package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.MembershipAlreadyExistsException;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.filter.MembershipFilter;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.MembershipService;
import cz.cvut.fit.household.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller which manages all requests related to users.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MembersController {

    private final HouseHoldService householdService;
    private final UserService userService;
    private final MembershipService membershipService;


    private static final String PENDING_HOUSEHOLDS_ATTR = "pendingHouseholds";
    private static final String ACTIVE_HOUSEHOLDS_ATTR = "activeHouseholds";
    private static final String RETURN_WELCOME_PAGE = "welcome";
    private static final String RETURN_INVITE_USER_PAGE = "members/inviteMember";

    @GetMapping("/household/{householdId}/{membershipId}")
    public String renderMemberProfilePage(@PathVariable Long householdId, @PathVariable Long membershipId, Model model) {
        Membership membership = membershipService.findMembershipById(membershipId)
                .orElseThrow(() -> new NonExistentEntityException("Membership with id: " + membershipId + " doesn't exist. "));

        model.addAttribute("membership", membership);
        return "members/profile";
    }

    @PostMapping("/{householdId}/users/search")
    public String searchForUser(@PathVariable Long householdId, @RequestParam String searchTerm, Model model) {

        List<User> users = userService.findUsersBySearchTerm(searchTerm);

        model.addAttribute("users", users);
        model.addAttribute("householdId", householdId);

        return RETURN_INVITE_USER_PAGE;
    }

    @GetMapping("/household/{householdId}/members")
    public String renderMembersPage(@PathVariable Long householdId, Model model) {

        MembershipFilter pendingMember = MembershipFilter.builder()
                .householdId(householdId)
                .status(MembershipStatus.PENDING)
                .build();

        MembershipFilter activeMember = MembershipFilter.builder()
                .householdId(householdId)
                .status(MembershipStatus.ACTIVE)
                .build();

        model.addAttribute("pendingMembers", membershipService.filterMemberships(pendingMember));
        model.addAttribute("activeMembers", membershipService.filterMemberships(activeMember));

        return "members/householdMembers";
    }

    @GetMapping("/household/{id}/invite")
    public String renderInviteUserPage(@PathVariable Long id, Model model) {
        model.addAttribute("householdId", id);

        return RETURN_INVITE_USER_PAGE;
    }

    @PostMapping("/household/{householdId}/invite")
    public String inviteUser(@PathVariable(name = "householdId") Long householdId, @RequestParam(name="username") String username, Model model) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with given username does not exist"));

        Household houseHold = householdService.findHouseHoldById(householdId)
                .orElseThrow(() -> new RuntimeException("Household with given id does not exist"));

        try {
            houseHold.getMemberships()
                    .forEach(membership -> {
                        if (membership.getUser().getUsername().equals(username) && membership.getStatus().getStatus().equals("ACTIVE")) {
                            throw new MembershipAlreadyExistsException("User with username: " + username + " is already a member of household with id: " + householdId);
                        }
                    });
        } catch (MembershipAlreadyExistsException e) {
            log.info("Trying to invite already a member of the household");
        }

        Membership membership = new Membership();
        membership.setStatus(MembershipStatus.PENDING);
        membership.setMembershipRole(MembershipRole.REGULAR);

        membershipService.createMembership(membership, user, houseHold);
        return RETURN_INVITE_USER_PAGE;
    }

    @GetMapping("household/{householdId}/invitation/{membershipId}/accept")
    public String acceptInvitation(Authentication authentication, @PathVariable Long householdId, @PathVariable Long membershipId, Model model) {
        User user = userService.findUserByUsername(authentication.getName())
                .orElseThrow(RuntimeException::new);

        membershipService.acceptInvitation(membershipId);

        List<Membership> pendingMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.PENDING))
                .collect(Collectors.toList());

        List<Membership> activeMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.ACTIVE))
                .collect(Collectors.toList());

        model.addAttribute(PENDING_HOUSEHOLDS_ATTR, pendingMemberships);
        model.addAttribute(ACTIVE_HOUSEHOLDS_ATTR, activeMemberships);
        return RETURN_WELCOME_PAGE;
    }


    @GetMapping("household/{householdId}/invitation/{membershipId}/decline")
    public String declineInvitation(Authentication authentication, @PathVariable Long householdId, @PathVariable Long membershipId, Model model) {
        User user = userService.findUserByUsername(authentication.getName())
                .orElseThrow(RuntimeException::new);

        membershipService.declineInvitation(membershipId);

        List<Membership> pendingMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.PENDING))
                .collect(Collectors.toList());

        List<Membership> activeMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.ACTIVE))
                .collect(Collectors.toList());

        model.addAttribute(PENDING_HOUSEHOLDS_ATTR, pendingMemberships);
        model.addAttribute(ACTIVE_HOUSEHOLDS_ATTR, activeMemberships);
        return RETURN_WELCOME_PAGE;
    }

    @GetMapping("/household/{id}/delete")
    public String leaveHousehold(Authentication authentication, @PathVariable Long id, Model model) {

        User user = userService.findUserByUsername(authentication.getName())
                .orElseThrow(RuntimeException::new);

        membershipService.leaveHousehold(id);

        List<Membership> pendingMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.PENDING))
                .collect(Collectors.toList());

        List<Membership> activeMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.ACTIVE))
                .collect(Collectors.toList());

        model.addAttribute(PENDING_HOUSEHOLDS_ATTR, pendingMemberships);
        model.addAttribute(ACTIVE_HOUSEHOLDS_ATTR, activeMemberships);
        return RETURN_WELCOME_PAGE;
    }

    @GetMapping("/household/{householdId}/member/{username}")
    @PreAuthorize("@authorizationService.canKick(#householdId, #username)")
    public String kickUser(@PathVariable("householdId") Long householdId, @PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new NonExistentEntityException("User with username: " + username + " doesn't exist"));

        Household household = householdService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException("Household with id: " + householdId + " doesn't exist"));

        Optional<Membership> membership = user.getMemberships().stream()
                .filter(m -> m.getHousehold().getId().equals(householdId) && m.getStatus().equals(MembershipStatus.ACTIVE))
                .findFirst();

        membership.ifPresent(value -> membershipService.declineInvitation(value.getId()));

        model.addAttribute("household", household);
        return "household/householdMain";
    }
}
