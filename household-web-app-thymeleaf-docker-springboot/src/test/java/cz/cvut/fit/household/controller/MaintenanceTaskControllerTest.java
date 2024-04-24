package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.config.MaintenanceConfig;
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
import cz.cvut.fit.household.service.AuthorizationService;
import cz.cvut.fit.household.service.interfaces.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(MaintenanceTaskController.class)
@ContextConfiguration
public class MaintenanceTaskControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HouseHoldService houseHoldService;
    @MockBean
    private MembershipService membershipService;
    @MockBean
    private MaintenanceService maintenanceService;
    @MockBean
    private MaintenanceTaskService maintenanceTaskService;
    @MockBean
    private MaintenanceConfig maintenanceConfig;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private AuthorizationService authorizationService;
    @MockBean
    private UserService userService;

    User user1 = new User("user1","1","User","user","user1@gmail.com",new ArrayList<>());
    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    Membership membership1 = new Membership(2L, MembershipStatus.ACTIVE, MembershipRole.OWNER,user1,household1);
    Maintenance maintenance1 = new Maintenance(3L, household1, new ArrayList<>(), "new maintenance", "description", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 1L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    List<Maintenance> maintenanceList = Collections.singletonList(maintenance1);
    //MaintenanceTask maintenanceTask2 = new MaintenanceTask(5L, "new maintenance", "description", maintenance1, membership1, false,membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(8).atZone(ZoneId.systemDefault()).toInstant()), false, Date.from(LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
    MaintenanceTask maintenanceTask = new MaintenanceTask(4L, "maintenanceTask", "SoHard", maintenance1, membership1, false,membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(8).atZone(ZoneId.systemDefault()).toInstant()), false, Date.from(LocalDate.now().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
    MaintenanceTaskCreationDTO maintenanceTaskCreationDTO = new MaintenanceTaskCreationDTO("maintenanceTask", "SoHard", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(8).atZone(ZoneId.systemDefault()).toInstant()));
    List<MaintenanceTask>maintenanceTaskList = Collections.singletonList(maintenanceTask);
    MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO(maintenanceTask.getTaskState(), maintenanceTask.getTaskResolution());


    @Before
    public void setUp() {
        household1.setMaintenances(maintenanceList);
        household1.setMemberships(Collections.singletonList(membership1));
        maintenance1.setMaintenanceTasks(maintenanceTaskList);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(userService.findUserByUsername(user1.getUsername())).thenReturn(java.util.Optional.ofNullable(user1));
        when(houseHoldService.findHouseHoldById(household1.getId())).thenReturn(java.util.Optional.ofNullable(household1));
        when(authorizationService.isOwner(household1)).thenReturn(true);
        when(membershipService.findAllMemberships()).thenReturn(Collections.singletonList(membership1));
        when(maintenanceService.findMaintenanceById(maintenance1.getId())).thenReturn(Optional.of(maintenance1));
        when(maintenanceTaskService.getListOfMaintenanceTaskDates(maintenance1)).thenReturn(Collections.singletonList(maintenanceTask.getStartDate()));
        when(maintenanceTaskService.findMaintenanceTaskById(maintenanceTask.getId())).thenReturn(Optional.ofNullable(maintenanceTask));
        //when(maintenanceTaskService.addMaintenanceTask(maintenance1)).thenReturn(maintenanceTask2);
        when(maintenanceTaskService.updateMaintenanceTask(maintenanceTask.getId(), maintenanceTaskCreationDTO)).thenReturn(maintenanceTask);
        when(maintenanceTaskService.changeTaskState(maintenanceStateDTO, maintenanceTask.getId())).thenReturn(maintenanceTask);
    }

    @Test
    public void addMaintenanceWhenGenerateAutoButton() throws Exception{
        MaintenanceTask maintenanceTask2 = maintenanceTaskService.addMaintenanceTask(maintenance1);
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/add", household1.getId(), maintenance1.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));
        verify(maintenanceTaskService, times(1)).addMaintenanceTask(maintenance1);
    }

    @Test
    public void renderEditMaintenanceTasksPage() throws Exception{
        MaintenanceTask maintenanceTask = new MaintenanceTask(4L, "maintenanceTask", "So hard", maintenance1, membership1, false, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/editIn", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                        .flashAttr("newMaintenanceTask", maintenanceTask)
                        .flashAttr("maintenanceTask", maintenanceTask))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/maintenanceTask/maintenanceTaskEditIn"));
    }

    @Test
    public void editMaintenanceTaskInPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/editIn", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                        .flashAttr("updatedMaintenanceTask", maintenanceTaskCreationDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId() + "/maintenanceTask/" + maintenanceTask.getId()));
        verify(maintenanceTaskService,times(1)).updateMaintenanceTask(maintenanceTask.getId(), maintenanceTaskCreationDTO);

    }

    @Test
    public void renderEditMaintenanceTaskPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/edit", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                        .flashAttr("newMaintenanceTask", maintenanceTask)
                        .flashAttr("maintenanceTask", maintenanceTask))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/maintenanceTask/maintenanceTaskEdit"));

    }

    @Test
    public void editMaintenanceTaskPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/edit", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                        .flashAttr("updatedMaintenanceTask", maintenanceTaskCreationDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));
        verify(maintenanceTaskService,times(1)).updateMaintenanceTask(maintenanceTask.getId(), maintenanceTaskCreationDTO);

    }

    @Test
    public void deleteMaintenanceTask() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/delete", household1.getId(), maintenance1.getId(), maintenanceTask.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));
        verify(maintenanceTaskService,times(1)).deleteMaintenanceTask(maintenanceTask.getId());

    }

    @Test
    public void renderMaintenanceTaskDetailsPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}", household1.getId(), maintenance1.getId(), maintenanceTask.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/maintenanceTask/maintenanceTaskDetails"));
    }

    @Test
    public void renderChangeStatePage() throws Exception{
       mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/changeState", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                       .flashAttr("newState", maintenanceStateDTO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/edit/maintenanceTaskStateEdit"));
    }

    @Test
    public void changeMaintenanceTaskState() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/changeState", household1.getId(), maintenance1.getId(), maintenanceTask.getId())
                        .flashAttr("updatedMaintenanceStateDTO", maintenanceStateDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId() + "/maintenanceTask/" + maintenanceTask.getId()));

    }

    @Test
    public void closeMaintenanceTask() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/stop", household1.getId(), maintenance1.getId(), maintenanceTask.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));

    }

    @Test
    public void returnToMaintenancePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/return", household1.getId(),maintenance1.getId(), maintenanceTask.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        verify(maintenanceService,times(1)).findMaintenanceById(maintenance1.getId());

    }
}