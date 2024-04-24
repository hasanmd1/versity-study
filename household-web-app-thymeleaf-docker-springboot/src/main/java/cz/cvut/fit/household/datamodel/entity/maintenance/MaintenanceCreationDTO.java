package cz.cvut.fit.household.datamodel.entity.maintenance;

import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCreationDTO {

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    private String description;


    private Membership assignee;


    private Membership reporter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @NotNull
    private Long frequency;

    private FrequencyPeriod frequencyPeriod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
