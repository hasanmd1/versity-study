package cz.cvut.fit.household.datamodel.entity.maintenance;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Maintenance items that define the general outline of the tasks that needs to be done around the household.
 */
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Household houseHoLD;

    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.PERSIST)
    private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    private String description;

    @ManyToOne
    private Membership assignee;

    @ManyToOne
    private Membership reporter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @NotNull
    private Long frequency;

    private FrequencyPeriod frequencyPeriod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotNull
    private boolean taskState;

    @NotNull
    private boolean taskResolution;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    public boolean getTaskState() {
        return taskState;
    }
    public  boolean getTaskResolution(){return taskResolution;}

}

