package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.*;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTaskCreationDTO;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.repository.maintenance.MaintenanceRepository;
import cz.cvut.fit.household.repository.maintenance.MaintenanceTaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MaintenanceTaskServiceImplTest {
    @Mock
    MaintenanceRepository maintenanceRepository;

    @Mock
    MaintenanceTaskRepository maintenanceTaskRepository;

    @InjectMocks
    MaintenanceTaskServiceImpl maintenanceTaskService;

    @InjectMocks
    MaintenanceServiceImpl maintenanceService;

    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    User user1 = new User("user1","1","User","1","user1@gmail.com",new ArrayList<>());
    Membership user1Membership= new Membership(1L, MembershipStatus.ACTIVE, MembershipRole.OWNER, user1,household1);
    Maintenance maintenance1 = new Maintenance(2L, household1, new ArrayList<>(), "new maintenance", "description", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(14).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    MaintenanceTask maintenanceTask = new MaintenanceTask(4L, "new maintenance", "description", maintenance1, user1Membership, false, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

    List<Maintenance> maintenanceList = Collections.singletonList(maintenance1);


    @Before
    public void setUp() throws Exception {
        household1.setMemberships(Collections.singletonList(user1Membership));
        household1.setMaintenances(maintenanceList);
        maintenance1.setMaintenanceTasks(Collections.singletonList(maintenanceTask));

        when(maintenanceTaskService.addMaintenanceTask(maintenance1)).thenReturn(maintenanceTask);
        when(maintenanceTaskRepository.findById(maintenanceTask.getId())).thenReturn(Optional.of(maintenanceTask));
        when(maintenanceTaskRepository.findAll()).thenReturn(Collections.singletonList(maintenanceTask));
        when(maintenanceTaskRepository.save(maintenanceTask)).thenReturn(maintenanceTask);
    }

    @Test
    public void addMaintenanceTask() {
        MaintenanceTask maintenanceTask = maintenanceTaskService.addMaintenanceTask(maintenance1);
        verify(maintenanceTaskRepository, times(1)).save(any(MaintenanceTask.class));
    }

    @Test
    public void findMaintenanceTaskById() {
        Optional<MaintenanceTask> maintenanceTask1 = maintenanceTaskService.findMaintenanceTaskById(maintenanceTask.getId());
        assertEquals(maintenanceTask1, Optional.of(maintenanceTask));
    }

    @Test
    public void updateMaintenanceTask() {
        MaintenanceTaskCreationDTO updatedMaintenanceTask = new MaintenanceTaskCreationDTO("maintenance", "description", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()));
        MaintenanceTask maintenanceTask1 = maintenanceTaskService.updateMaintenanceTask(maintenanceTask.getId(), updatedMaintenanceTask);

        ArgumentCaptor<MaintenanceTask>argumentCaptor = ArgumentCaptor.forClass(MaintenanceTask.class);
        verify(maintenanceTaskRepository).save(argumentCaptor.capture());
        assertEquals(maintenanceTask1.getTitle(), argumentCaptor.getValue().getTitle());
        assertEquals(maintenanceTask1.getDescription(), argumentCaptor.getValue().getDescription());
        assertEquals(maintenanceTask1.getAssignee(), argumentCaptor.getValue().getAssignee());
        assertEquals(maintenanceTask1.getReporter(), argumentCaptor.getValue().getReporter());
        assertEquals(maintenanceTask1.getDeadline(), argumentCaptor.getValue().getDeadline());
        assertEquals(maintenanceTask1.getMaintenance(), argumentCaptor.getValue().getMaintenance());
        assertEquals(maintenanceTask1.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
        assertEquals(maintenanceTask1.getTaskState(), argumentCaptor.getValue().getTaskState());

    }

    @Test
    public void deleteMaintenanceTask() {
        maintenanceTaskRepository.deleteById(maintenanceTask.getId());
        verify(maintenanceTaskRepository, times(1)).deleteById(maintenanceTask.getId());
    }

    @Test
    public void closeMaintenanceTask() {
        maintenanceTaskService.closeMaintenanceTask(maintenanceTask);
        ArgumentCaptor<MaintenanceTask> argumentCaptor = ArgumentCaptor.forClass(MaintenanceTask.class);
        verify(maintenanceTaskRepository).save(argumentCaptor.capture());
        assertEquals(maintenanceTask.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
        assertEquals(maintenanceTask.getTaskState(), argumentCaptor.getValue().getTaskState());
    }

    @Test
    public void changeTaskState() {
        MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO(false, true);

        MaintenanceTask maintenanceTask1 = maintenanceTaskService.changeTaskState(maintenanceStateDTO, maintenanceTask.getId());

        ArgumentCaptor<MaintenanceTask> argumentCaptor = ArgumentCaptor.forClass(MaintenanceTask.class);
        verify(maintenanceTaskRepository, times(2)).save(argumentCaptor.capture());

        assertEquals(maintenanceTask.getTaskState(), argumentCaptor.getValue().getTaskState());
        assertEquals(maintenanceTask.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
    }

    @Test
    public void generateAutoTask() {
        MaintenanceTask maintenanceTask1 = maintenanceTaskService.generateAutoTask(maintenance1, maintenanceTask.getStartDate(), maintenanceTask.getDeadline());
        verify(maintenanceTaskRepository, times(1)).save(any(MaintenanceTask.class));

    }

    @Test
    public void deleteWithMaintenance() {
        maintenanceTaskService.deleteWithMaintenance(maintenance1);
        verify(maintenanceTaskRepository, times(1)).deleteById(maintenanceTask.getId());
    }

    @Test
    public void getDates() {
        List<Date> dateList = maintenanceTaskService.getDates(maintenance1.getStartDate(), maintenance1.getEndDate(), 1L, FrequencyPeriod.WEEKLY);
        Date now = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).plusDays(14).toInstant());
        List<Date> dateListManual = maintenanceTaskService.getDates(now, end, 1L, FrequencyPeriod.WEEKLY);
        assertEquals(dateListManual, dateList);
    }

    @Test
    public void getNextDate() {
        Date dateList = maintenanceTaskService.getNextDate(maintenance1.getStartDate(), maintenance1.getEndDate(), 1L, FrequencyPeriod.WEEKLY);
        Date now = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).plusDays(14).toInstant());
        Date dateListManual = maintenanceTaskService.getNextDate(now, end, 1L, FrequencyPeriod.WEEKLY);
        assertEquals(dateListManual, dateList);
    }

    @Test
    public void getListOfMaintenanceTaskDates(){
        List<Date> manual = new ArrayList<>();
        manual.add(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        List<Date> auto = maintenanceTaskService.getListOfMaintenanceTaskDates(maintenance1);
        assertEquals(manual, auto);
    }

    @Test
    public void calculateDifferent() {
        Long diff = maintenanceTaskService.calculateDifferent(maintenance1.getStartDate(), maintenance1.getDeadline());
        Date now = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).plusDays(7).toInstant());
        Long manual = maintenanceTaskService.calculateDifferent(now, end);
        assertEquals(manual, diff);
    }
}