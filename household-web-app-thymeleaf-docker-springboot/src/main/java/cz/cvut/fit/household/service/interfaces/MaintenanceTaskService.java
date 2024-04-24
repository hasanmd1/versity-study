package cz.cvut.fit.household.service.interfaces;

import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTaskCreationDTO;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MaintenanceTaskService {

    MaintenanceTask addMaintenanceTask(Maintenance maintenance);

    MaintenanceTask generateAutoTask(Maintenance maintenance, Date startDate, Date deadline);

    void deleteMaintenanceTask(Long maintenanceTaskId);

    void closeMaintenanceTask(MaintenanceTask maintenancetask);

    MaintenanceTask changeTaskState(MaintenanceStateDTO updatedMaintenanceStateDTO, Long maintenanceTaskId);

    Optional<MaintenanceTask> findMaintenanceTaskById(Long id);

    MaintenanceTask updateMaintenanceTask(Long maintenanceTaskId, MaintenanceTaskCreationDTO updatedMaintenanceTask);

    List<Date> getDates(Date start, Date end, Long frequency, FrequencyPeriod frequencyPeriod);

    Long calculateDifferent(Date start, Date end);

    void deleteWithMaintenance(Maintenance maintenance);

    List<Date> getListOfMaintenanceTaskDates(Maintenance maintenance);

    Date getNextDate(Date start, Date end, Long frequency, FrequencyPeriod frequencyPeriod);

}
