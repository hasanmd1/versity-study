package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.filter.MembershipFilter;
import cz.cvut.fit.household.repository.membership.jpa.MembershipRepository;
import cz.cvut.fit.household.service.interfaces.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    @Autowired
    public MembershipServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Membership createMembership(Membership membership, User user, Household houseHold) {
        user.addMembership(membership);
        houseHold.addMembership(membership);

        return membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public List<Membership> filterMemberships(MembershipFilter membershipFilter) {
        return membershipRepository.filterMemberships(membershipFilter);
    }

    @Override
    public void acceptInvitation(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new NonExistentEntityException("Membership with id:- " + membershipId + " doesn't exist."));

        membership.setStatus(MembershipStatus.ACTIVE);
        membershipRepository.save(membership);
    }

    @Override
    public void declineInvitation(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new NonExistentEntityException("Membership with id: " + membershipId + " doesn't exist"));

        membership.setStatus(MembershipStatus.DISABLED);
        membershipRepository.save(membership);
    }

    @Override
    public void leaveHousehold(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new NonExistentEntityException("Membership with id: " + id + " doesn't exist"));

        membership.setStatus(MembershipStatus.DISABLED);
        membershipRepository.save(membership);
    }

    @Override
    public List<Membership> findAllMemberships() {
        return membershipRepository.findAll();
    }

    @Override
    public List<Membership> findMembershipsByUsername(String username) {
        return membershipRepository.findMembershipsByUsername(username);
    }

    @Override
    public Optional<Membership> findMembershipById(Long membershipId) {
        return membershipRepository.findById(membershipId);
    }

    @Override
    public Membership findMembershipsById(Long membershipId){
        return membershipRepository.findMembershipsWithId(membershipId);
    }
}
