package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTaskCreationDTO;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.repository.maintenance.MaintenanceTaskRepository;
import cz.cvut.fit.household.service.interfaces.MaintenanceTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    @Override
    public MaintenanceTask addMaintenanceTask(Maintenance maintenance){
        List<Date>possibleDates = getDates(maintenance.getStartDate(), maintenance.getEndDate(), maintenance.getFrequency(), maintenance.getFrequencyPeriod());
        List<Date> existingTaskDates = getListOfMaintenanceTaskDates(maintenance);

        possibleDates.removeAll(existingTaskDates);
        Date startDate = Collections.min(possibleDates);
        MaintenanceTask maintenanceTask = new MaintenanceTask();

        maintenanceTask.setMaintenance(maintenance);
        maintenanceTask.setTitle(maintenance.getTitle());
        maintenanceTask.setDescription(maintenance.getDescription());
        maintenanceTask.setTaskResolution(false);
        maintenanceTask.setTaskState(false);
        maintenanceTask.setReporter(maintenance.getReporter());
        maintenanceTask.setAssignee(maintenance.getAssignee());
        maintenanceTask.setStartDate(startDate);
        maintenanceTask.setDeadline(Date.from(maintenanceTask.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(calculateDifferent(maintenance.getStartDate(), maintenance.getDeadline())).atZone(ZoneId.systemDefault()).toInstant()));

        return maintenanceTaskRepository.save(maintenanceTask);
    }

    @Override
    public Optional<MaintenanceTask> findMaintenanceTaskById(Long id) {
        return maintenanceTaskRepository.findById(id);
    }

    @Override
    public MaintenanceTask updateMaintenanceTask(Long maintenanceTaskId, MaintenanceTaskCreationDTO updatedMaintenanceTask) {

        MaintenanceTask maintenancetask = findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException("Maintenance with id: " + maintenanceTaskId + " doesn't exist"));

        maintenancetask.setTitle(updatedMaintenanceTask.getTitle());
        maintenancetask.setDescription(updatedMaintenanceTask.getDescription());
        maintenancetask.setAssignee(updatedMaintenanceTask.getAssignee());
        maintenancetask.setReporter(updatedMaintenanceTask.getReporter());
        maintenancetask.setDeadline(updatedMaintenanceTask.getDeadline());

        return maintenanceTaskRepository.save(maintenancetask);
    }

    @Override
    public void deleteMaintenanceTask(Long maintenanceTaskId){
        maintenanceTaskRepository.deleteById(maintenanceTaskId);
    }

    @Override
    public void closeMaintenanceTask(MaintenanceTask maintenanceTask){
        maintenanceTask.setTaskResolution(maintenanceTask.getTaskResolution());
        maintenanceTask.setTaskState(true);
        maintenanceTaskRepository.save(maintenanceTask);
    }

    @Override
    public MaintenanceTask changeTaskState(MaintenanceStateDTO updatedMaintenanceStateDTO, Long maintenanceTaskId) {

        MaintenanceTask maintenanceTask = findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException("Maintenance with id: " + maintenanceTaskId + " doesn't exist"));

        maintenanceTask.setTaskState(updatedMaintenanceStateDTO.getTaskState());
        maintenanceTask.setTaskResolution(updatedMaintenanceStateDTO.getTaskResolution());

        if(maintenanceTask.getTaskResolution()){
            List<Date> possibleDate = getDates(maintenanceTask.getMaintenance().getStartDate(), maintenanceTask.getMaintenance().getEndDate(), maintenanceTask.getMaintenance().getFrequency(), maintenanceTask.getMaintenance().getFrequencyPeriod());
            possibleDate.removeAll(getListOfMaintenanceTaskDates(maintenanceTask.getMaintenance()));
            Date nextDate = getNextDate(maintenanceTask.getStartDate(), maintenanceTask.getMaintenance().getEndDate(), maintenanceTask.getMaintenance().getFrequency(), maintenanceTask.getMaintenance().getFrequencyPeriod());
            if(possibleDate.contains(nextDate)){
                addMaintenanceTask(maintenanceTask.getMaintenance());
            }
        }

        return maintenanceTaskRepository.save(maintenanceTask);
    }

    @Override
    public MaintenanceTask generateAutoTask(Maintenance maintenance, Date startDate, Date deadline){
        MaintenanceTask maintenanceTask = new MaintenanceTask();

        maintenanceTask.setTitle(maintenance.getTitle());
        maintenanceTask.setDescription(maintenance.getDescription());
        maintenanceTask.setTaskResolution(false);
        maintenanceTask.setTaskState(false);
        maintenanceTask.setReporter(maintenance.getReporter());
        maintenanceTask.setAssignee(maintenance.getAssignee());
        maintenanceTask.setMaintenance(maintenance);
        maintenanceTask.setStartDate(startDate);
        maintenanceTask.setDeadline(deadline);

        return maintenanceTaskRepository.save(maintenanceTask);
    }

    @Override
    public void deleteWithMaintenance(Maintenance maintenance){
        List<MaintenanceTask> maintenanceTaskList = maintenanceTaskRepository.findAll().stream().filter(m1 -> m1.getMaintenance().equals(maintenance)).collect(Collectors.toList());

        for(MaintenanceTask allMaintenanceTask: maintenanceTaskList){
            if(allMaintenanceTask != null){
                maintenanceTaskRepository.deleteById(allMaintenanceTask.getId());
            }
        }
    }

    @Override
    public Date getNextDate(Date start, Date end, Long frequency, FrequencyPeriod frequencyPeriod){
        switch (frequencyPeriod){
            case DAILY:
                start = Date.from(start.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusDays(frequency)
                        .with(LocalTime.MIDNIGHT)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                return start;
            case MONTHLY:
                start = Date.from(start.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusMonths(frequency)
                        .with(LocalTime.MIDNIGHT)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                return start;
            case WEEKLY:
                start = Date.from(start.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusWeeks(frequency)
                        .with(LocalTime.MIDNIGHT)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                return start;
            case YEARLY:
                start = Date.from(start.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusYears(frequency)
                        .with(LocalTime.MIDNIGHT)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                return start;
            default:
                throw new IllegalArgumentException("Invalid frequency period");
        }
    }

    @Override
    public List<Date> getDates(Date start, Date end, Long frequency, FrequencyPeriod frequencyPeriod){
        List<Date> nextDates = new ArrayList<>();
        while(start.before(end)){
            switch (frequencyPeriod){
                case DAILY:
                    start = Date.from(start.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .plusDays(frequency)
                            .with(LocalTime.MIDNIGHT)
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    nextDates.add(start);
                    break;
                case MONTHLY:
                    start = Date.from(start.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .plusMonths(frequency)
                            .with(LocalTime.MIDNIGHT)
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    nextDates.add(start);
                    break;
                case WEEKLY:
                    start = Date.from(start.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .plusWeeks(frequency)
                            .with(LocalTime.MIDNIGHT)
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    nextDates.add(start);
                    break;
                case YEARLY:
                    start = Date.from(start.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .plusYears(frequency)
                            .with(LocalTime.MIDNIGHT)
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    nextDates.add(start);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid frequency period");
            }

        }
        return nextDates;
    }

    @Override
    public List<Date> getListOfMaintenanceTaskDates(Maintenance maintenance){
        List<Date> dates = new ArrayList<>();
        for(MaintenanceTask maintenanceTask: maintenance.getMaintenanceTasks()){
            dates.add(maintenanceTask.getStartDate());
        }
        return dates;
    }

    @Override
    public Long calculateDifferent(Date start, Date end){
        return Duration.between(LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()),LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault())).toDays();
    }


}
