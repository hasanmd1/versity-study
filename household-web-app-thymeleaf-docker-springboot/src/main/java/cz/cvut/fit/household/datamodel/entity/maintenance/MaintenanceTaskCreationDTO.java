package cz.cvut.fit.household.datamodel.entity.maintenance;

import cz.cvut.fit.household.datamodel.entity.Membership;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTaskCreationDTO {

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    private String description;

    private Membership assignee;

    private Membership reporter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
}
