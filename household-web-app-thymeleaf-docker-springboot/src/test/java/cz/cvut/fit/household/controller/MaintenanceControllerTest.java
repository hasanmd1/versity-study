package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.config.MaintenanceConfig;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceCreationDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.Membership;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
@WebMvcTest(MaintenanceController.class)
@ContextConfiguration
public class MaintenanceControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private HouseHoldService houseHoldService;

    @MockBean
    private MembershipService membershipService;

    @MockBean
    private MaintenanceService maintenanceService;

    @MockBean
    private MaintenanceConfig maintenanceConfig;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    User user1 = new User("user1","1","User","1","user1@gmail.com",new ArrayList<>());
    Household household1 = new Household(1L,"user1 household", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    Membership membership1 = new Membership(1L, MembershipStatus.ACTIVE, MembershipRole.OWNER,user1,household1);
    Maintenance maintenance1 = new Maintenance(2L, household1, new ArrayList<>(), "new maintenance1", "description1", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(14).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    Maintenance maintenance2 = new Maintenance(3L, household1, new ArrayList<>(), "new maintenance2", "description2", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 1L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()), false, false, Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    List<Maintenance> maintenanceList = Arrays.asList(maintenance2, maintenance1);
    MaintenanceCreationDTO maintenanceCreationDTO1 = new MaintenanceCreationDTO("new maintenance1", "description1", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 2L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(14).atZone(ZoneId.systemDefault()).toInstant()));
    MaintenanceCreationDTO maintenanceCreationDTO2 = new MaintenanceCreationDTO("new maintenance2", "description2", membership1, membership1, Date.from(LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()).toInstant()), 1L, FrequencyPeriod.DAILY, Date.from(LocalDate.now().atStartOfDay().plusDays(10).atZone(ZoneId.systemDefault()).toInstant()));

    MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO(maintenance1.getTaskState(), maintenance1.getTaskResolution());

    @Before
    public void setUp() throws Exception {
        household1.setMemberships(Collections.singletonList(membership1));
        household1.setMaintenances(maintenanceList);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        when(userService.findUserByUsername(user1.getUsername())).thenReturn(java.util.Optional.ofNullable(user1));
        when(houseHoldService.findHouseHoldById(household1.getId())).thenReturn(java.util.Optional.ofNullable(household1));
        when(authorizationService.isOwner(household1)).thenReturn(true);
        when(membershipService.findAllMemberships()).thenReturn(Collections.singletonList(membership1));
        when(maintenanceService.findMaintenanceById(maintenance1.getId())).thenReturn(Optional.of(maintenance1));
        when(maintenanceService.stopGeneratingMaintenance(maintenance1)).thenReturn(maintenance1);
        when(maintenanceService.changeState(maintenanceStateDTO, maintenance1.getId())).thenReturn(maintenance1);
        when(maintenanceService.addMaintenance(maintenanceCreationDTO1, household1, maintenance1.getAssignee(), maintenance1.getReporter())).thenReturn(maintenance1);
        when(maintenanceService.updateMaintenance(maintenance1.getId(), maintenanceCreationDTO1, household1, maintenance1.getReporter(), maintenance1.getAssignee())).thenReturn(maintenance1);

    }

    @Test
    public void renderMaintenancesPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance", household1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/maintenanceView"));
    }

    @Test
    public void renderAddMaintenancePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/add", household1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/addMaintenance"));
    }

    @Test
    public void getAddMaintenancePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance", household1.getId())
                        .flashAttr("updatedMaintenance", maintenanceCreationDTO1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance"));
        verify(houseHoldService,times(1)).findHouseHoldById(household1.getId());
        verify(maintenanceService,times(1)).addMaintenance(maintenanceCreationDTO1, household1, maintenance1.getReporter(), maintenance1.getAssignee());

    }

    @Test
    public void renderEditMaintenanceInPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/editIn", household1.getId(), maintenance1.getId())
                        .flashAttr("maintenance", maintenance1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/edit/maintenanceEditIn"));
    }

    @Test
    public void editMaintenanceInPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/editIn", household1.getId(), maintenance1.getId())
                        .flashAttr("updatedMaintenance", maintenanceCreationDTO1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));
        verify(maintenanceService,times(1)).updateMaintenance(maintenance1.getId(), maintenanceCreationDTO1, household1, maintenance1.getReporter(), maintenance1.getAssignee());

    }

    @Test
    public void renderEditMaintenancePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/edit", household1.getId(), maintenance1.getId())
                        .flashAttr("maintenance", maintenance1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/edit/maintenanceEdit"));

    }

    @Test
    public void getEditMaintenancePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/edit", household1.getId(), maintenance1.getId())
                        .flashAttr("updatedMaintenance", maintenanceCreationDTO1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance"));
        verify(maintenanceService,times(1)).updateMaintenance(maintenance1.getId(), maintenanceCreationDTO1, household1, maintenance1.getReporter(), maintenance1.getAssignee());

    }

    @Test
    public void deleteMaintenance() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/delete", household1.getId(), maintenance1.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance"));
        verify(maintenanceService,times(1)).deleteMaintenance(maintenance1.getId());
    }

    @Test
    public void renderMaintenanceDetailsPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}", household1.getId(), maintenance1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/maintenanceDetails"));
    }

    @Test
    public void renderChangeStatePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/changeState", household1.getId(), maintenance1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("maintenance/edit/maintenanceStateEdit"));
    }

    @Test
    public void getChangeMaintenanceState() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/household/{householdId}/maintenance/{maintenanceId}/changeState", household1.getId(), maintenance1.getId())
                        .flashAttr("updatedMaintenanceStateDTO", maintenanceStateDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance/" + maintenance1.getId()));

    }

    @Test
    public void stopGeneratingMaintenanceTask() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/stop", household1.getId(), maintenance1.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/household/" + household1.getId() + "/maintenance"));

    }

    @Test
    public void returnToMaintenance() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/household/{householdId}/maintenance/{maintenanceId}/return", household1.getId(),maintenance1.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        verify(maintenanceService,times(1)).findMaintenanceById(maintenance1.getId());
    }
}