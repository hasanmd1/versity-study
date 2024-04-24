package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceCreationDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.maintenance.MaintenanceRepository;
import cz.cvut.fit.household.service.interfaces.MaintenanceService;
import cz.cvut.fit.household.service.interfaces.MaintenanceTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceTaskService maintenanceTaskService;

    private final MaintenanceRepository maintenanceRepository;


    @Override
    public Maintenance addMaintenance(MaintenanceCreationDTO updatedMaintenance, Household household, Membership reporter, Membership assignee) {

        Maintenance maintenance = new Maintenance();

        maintenance.setTitle(updatedMaintenance.getTitle());
        maintenance.setDescription(updatedMaintenance.getDescription());
        maintenance.setAssignee(assignee);
        maintenance.setReporter(reporter);
        maintenance.setHouseHoLD(household);
        maintenance.setTaskState(false);
        maintenance.setDeadline(updatedMaintenance.getDeadline());
        maintenance.setFrequency(updatedMaintenance.getFrequency());
        maintenance.setFrequencyPeriod(updatedMaintenance.getFrequencyPeriod());
        maintenance.setEndDate(updatedMaintenance.getEndDate());
        maintenance.setStartDate(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        maintenance.setTaskResolution(false);

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance updateMaintenance(Long maintenanceId, MaintenanceCreationDTO updatedMaintenance, Household household, Membership reporter, Membership assignee) {

        Maintenance maintenance = findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException("Maintenance with id:- " + maintenanceId + " doesn't exist."));

        maintenance.setTitle(updatedMaintenance.getTitle());
        maintenance.setDescription(updatedMaintenance.getDescription());
        maintenance.setAssignee(assignee);
        maintenance.setReporter(reporter);
        maintenance.setHouseHoLD(household);
        maintenance.setDeadline(updatedMaintenance.getDeadline());
        maintenance.setFrequency(updatedMaintenance.getFrequency());
        maintenance.setFrequencyPeriod(updatedMaintenance.getFrequencyPeriod());
        maintenance.setEndDate(updatedMaintenance.getEndDate());

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public void deleteMaintenance(Long maintenanceId){
        Maintenance maintenance = findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException("Maintenance with id: " + maintenanceId + " doesn't exist"));

        maintenanceTaskService.deleteWithMaintenance(maintenance);
        maintenanceRepository.deleteById(maintenanceId);
    }

    @Override
    public Maintenance stopGeneratingMaintenance(Maintenance maintenance){
        maintenance.setTaskResolution(maintenance.getTaskResolution());
        maintenance.setTaskState(true);
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance changeState(MaintenanceStateDTO updatedMaintenanceStateDTO, Long maintenanceId) {

        Maintenance maintenance = findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException("Maintenance with id: " + maintenanceId + " doesn't exist"));

        maintenance.setTaskState(updatedMaintenanceStateDTO.getTaskState());
        maintenance.setTaskResolution(updatedMaintenanceStateDTO.getTaskResolution());

        if(maintenance.getTaskResolution() && !maintenance.getTaskState()){
            List<Date> possibleDate = maintenanceTaskService.getDates(maintenance.getStartDate(), maintenance.getEndDate(), maintenance.getFrequency(), maintenance.getFrequencyPeriod());
            possibleDate.removeAll(maintenanceTaskService.getListOfMaintenanceTaskDates(maintenance));
            Date nextDate = maintenanceTaskService.getNextDate(maintenance.getStartDate(), maintenance.getEndDate(), maintenance.getFrequency(), maintenance.getFrequencyPeriod());
            if(possibleDate.contains(nextDate)){
                MaintenanceTask maintenanceTask = maintenanceTaskService.addMaintenanceTask(maintenance);
                maintenance.getMaintenanceTasks().add(maintenanceTask);
            }
        }
        return maintenanceRepository.save(maintenance);
    }


    @Override
    public Optional<Maintenance> findMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

    @Override
    public List<Maintenance> getAll(){
        return maintenanceRepository.findAll();
    }

}
