package cz.cvut.fit.household.datamodel.entity.household;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.category.Category;
import cz.cvut.fit.household.datamodel.entity.location.Location;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Household is the main class of the application. Its purpose is to save general information about household and links
 * to such entities as Maintenance, Location, Memberships. All of mentioned entities can not exist without household.
 *
 * @see Membership
 * @see Maintenance
 * @see Location
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Household {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Title is empty")
    private String title;

    private String description;

    @OneToMany(mappedBy = "household", cascade = CascadeType.REMOVE)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "houseHold", cascade = CascadeType.PERSIST)
    private List<Location> locations;

    @OneToMany(mappedBy = "houseHolD", cascade = CascadeType.PERSIST)
    private List<Category> category;

    @OneToMany(mappedBy = "houseHoLD", cascade = CascadeType.PERSIST)
    private List<Maintenance> maintenances;

    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setHousehold(this);
    }
}
