package cz.cvut.fit.household.datamodel.entity.maintenance;

import cz.cvut.fit.household.datamodel.entity.Membership;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Maintenance maintenance;

    @ManyToOne
    private Membership assignee;

    @NotNull
    private boolean taskResolution;

    @ManyToOne
    private Membership reporter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @NotNull
    private boolean taskState;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    public  boolean getTaskResolution(){return taskResolution;}

    public boolean getTaskState() {
        return taskState;
    }
}
