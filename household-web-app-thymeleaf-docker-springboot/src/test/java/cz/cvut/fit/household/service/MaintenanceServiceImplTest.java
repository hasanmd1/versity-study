package cz.cvut.fit.household.service;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceCreationDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.enums.FrequencyPeriod;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.repository.maintenance.MaintenanceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MaintenanceServiceImplTest {

    @Mock
    MaintenanceRepository maintenanceRepository;

    @Mock
    MaintenanceTaskServiceImpl maintenanceTaskService;

    @InjectMocks
    MaintenanceServiceImpl maintenanceService;

    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    User user1 = new User("user1","1","User","1","user1@gmail.com",new ArrayList<>());
    Membership user1Membership= new Membership(1L, MembershipStatus.ACTIVE, MembershipRole.REGULAR, user1,household1);
    Maintenance maintenance1 = new Maintenance(2L, household1, new ArrayList<>(), "new maintenance1", "description1", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(14).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    Maintenance maintenance2 = new Maintenance(3L, household1, new ArrayList<>(), "new maintenance2", "description2", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 1L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    List<Maintenance> maintenanceList = Arrays.asList(maintenance2, maintenance1);
    MaintenanceCreationDTO maintenanceCreationDTO1 = new MaintenanceCreationDTO("new maintenance1", "description1", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(14).atZone(ZoneId.systemDefault()).toInstant()));
    MaintenanceCreationDTO maintenanceCreationDTO2 = new MaintenanceCreationDTO("new maintenance2", "description2", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 1L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()));


    @Before
    public void setUp(){
        household1.setMemberships(Collections.singletonList(user1Membership));
        household1.setMaintenances(Arrays.asList(maintenance1, maintenance2));

        when(maintenanceRepository.findById(maintenance1.getId())).thenReturn(Optional.of(maintenance1));
        when(maintenanceRepository.findAll()).thenReturn(maintenanceList);
        when(maintenanceRepository.save(maintenance1)).thenReturn(maintenance1);
        doNothing().when(maintenanceRepository).deleteById(maintenance1.getId());
    }

    @Test
    public void addMaintenance() {
        Maintenance maintenance = maintenanceService.addMaintenance(maintenanceCreationDTO1, household1, user1Membership, user1Membership);
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));
    }

    @Test
    public void updateMaintenance() {
        MaintenanceCreationDTO updatedMaintenanceDTO = new MaintenanceCreationDTO("new updatedMaintenance1", "description1", user1Membership, user1Membership, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.WEEKLY, Date.from(LocalDate.now().atStartOfDay().plusDays(16).atZone(ZoneId.systemDefault()).toInstant()));
        Maintenance updatedMaintenance = maintenanceService.updateMaintenance(maintenance1.getId(), updatedMaintenanceDTO, household1, user1Membership, user1Membership);
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));

        ArgumentCaptor<Maintenance> argumentCaptor = ArgumentCaptor.forClass(Maintenance.class);
        verify(maintenanceRepository).save(argumentCaptor.capture());
        assertEquals(updatedMaintenance.getTitle(), argumentCaptor.getValue().getTitle());
        assertEquals(updatedMaintenance.getDescription(), argumentCaptor.getValue().getDescription());
        assertEquals(updatedMaintenance.getEndDate(), argumentCaptor.getValue().getEndDate());
        assertEquals(updatedMaintenance.getDeadline(), argumentCaptor.getValue().getDeadline());
        assertEquals(updatedMaintenance.getAssignee(), argumentCaptor.getValue().getAssignee());
        assertEquals(updatedMaintenance.getReporter(), argumentCaptor.getValue().getReporter());
        assertEquals(updatedMaintenance.getHouseHoLD(), argumentCaptor.getValue().getHouseHoLD());
        assertEquals(updatedMaintenance.getMaintenanceTasks(), argumentCaptor.getValue().getMaintenanceTasks());
        assertEquals(updatedMaintenance.getTaskState(), argumentCaptor.getValue().getTaskState());
        assertEquals(updatedMaintenance.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
        assertEquals(updatedMaintenance.getFrequency(), argumentCaptor.getValue().getFrequency());
        assertEquals(updatedMaintenance.getFrequencyPeriod(), argumentCaptor.getValue().getFrequencyPeriod());
    }

    @Test
    public void deleteMaintenance() {
        maintenanceService.deleteMaintenance(maintenance1.getId());
        verify(maintenanceRepository, times(1)).deleteById(maintenance1.getId());
    }

    @Test
    public void stopGeneratingMaintenance() {
        Maintenance maintenance = maintenanceService.stopGeneratingMaintenance(maintenance1);

        ArgumentCaptor<Maintenance> argumentCaptor = ArgumentCaptor.forClass(Maintenance.class);
        verify(maintenanceRepository).save(argumentCaptor.capture());
        assertEquals(maintenance.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
        assertEquals(maintenance.getTaskState(), argumentCaptor.getValue().getTaskState());
    }

    @Test
    public void changeState() {
        MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO(true, true);

        Maintenance maintenance = maintenanceService.changeState(maintenanceStateDTO, maintenance1.getId());

        ArgumentCaptor<Maintenance> argumentCaptor = ArgumentCaptor.forClass(Maintenance.class);
        verify(maintenanceRepository).save(argumentCaptor.capture());

        assertEquals(maintenance.getTaskState(), argumentCaptor.getValue().getTaskState());
        assertEquals(maintenance.getTaskResolution(), argumentCaptor.getValue().getTaskResolution());
    }

    @Test
    public void findMaintenanceById() {
        Optional<Maintenance> maintenance = maintenanceService.findMaintenanceById(maintenance1.getId());
        assertEquals(maintenance, Optional.of(maintenance1));
    }

    @Test
    public void getAll() {
        List<Maintenance> result = maintenanceService.getAll();
        assertEquals(result, Arrays.asList(maintenance2, maintenance1));
    }
}