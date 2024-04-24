package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.config.MaintenanceConfig;
import cz.cvut.fit.household.datamodel.entity.*;
import cz.cvut.fit.household.datamodel.entity.events.OnInventoryChangeEvent;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTaskCreationDTO;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.AuthorizationService;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.MaintenanceService;
import cz.cvut.fit.household.service.interfaces.MaintenanceTaskService;
import cz.cvut.fit.household.service.interfaces.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class MaintenanceTaskController {
    private final HouseHoldService houseHoldService;
    private final MembershipService membershipService;
    private final MaintenanceService maintenanceService;
    private final MaintenanceTaskService maintenanceTaskService;
    private final MaintenanceConfig maintenanceConfig;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthorizationService authorizationService;

    private static final String MAINTENANCE_ATTR = "maintenance";
    private static final String NEW_STATE_ATTR = "newState";
    private static final String NEW_MAINTENANCE_TASK_ATTR = "newMaintenanceTask";
    private static final String HOUSEHOLD_ATTR = "household";
    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String MAINTENANCE_WITH_ID = "Maintenance With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String OWNER_PERMISSION = "permission";
    private static final String REJECT_DEADLINE = "rejectDeadline";
    private static final String REJECT_MESSAGE = "rejectMessage";
    private static final String ASSIGNEE = "assignee";
    private static final String REPORTER = "reporter";
    private static final String MAINTENANCE_TASK_ATTR = "maintenanceTask";
    private static final String MAINTENANCE_TASK_TITLE = "Maintenance Task with title: ";
    private static final String REDIRECTION_HOUSEHOLD = "redirect:/household/";
    private static final String REDIRECT_TO_MAINTENANCE_DETAILS = "redirect:/household/{householdId}/maintenance/{maintenanceId}";
    private static final String RETURN_MAINTENANCE_TASK_DETAILS = "maintenance/maintenanceTask/maintenanceTaskDetails";
    private static final String RETURN_EDIT_IN_MAINTENANCE_TASK = "maintenance/maintenanceTask/maintenanceTaskEditIn";
    private static final String RETURN_STATE_EDIT_MAINTENANCE = "maintenance/edit/maintenanceStateEdit";
    private static final String RETURN_EDIT_MAINTENANCE_TASK = "maintenance/maintenanceTask/maintenanceTaskEdit";
    private static final String REJECT_DEADLINE_MSG = "Deadline must not be a date before today and not be empty";
    private static final String REJECT_MESSAGE_MSG = "Task State cannot be set to close when it is not resolved yet";


    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/add")
    public String addMaintenanceWhenGenerateAutoButton(@PathVariable Long householdId,
                                                       @PathVariable Long maintenanceId,
                                                       ModelMap model,
                                                       RedirectAttributes redirectAttributes){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));
        List<Date> possibleDates = maintenanceTaskService.getDates(maintenance.getStartDate(), maintenance.getEndDate(), maintenance.getFrequency(), maintenance.getFrequencyPeriod());
        List<Date> existingDates = maintenanceTaskService.getListOfMaintenanceTaskDates(maintenance);
        possibleDates.removeAll(existingDates);
        if(!possibleDates.isEmpty()){
            MaintenanceTask maintenanceTask = maintenanceTaskService.addMaintenanceTask(maintenance);
            maintenance.getMaintenanceTasks().add(maintenanceTask);
            maintenanceConfig.maintenanceEmailProcessing(maintenanceTask.getAssignee(), maintenanceTask.getReporter(), maintenanceTask.getTitle(), "Maintenance Task Created", "was created");
            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "Maintenance task with title: " + maintenanceTask.getTitle() + " created. Reporter: " + maintenanceTask.getReporter().getUser().getUsername() + ", assignee :- " + maintenanceTask.getAssignee().getUser().getUsername() + ", Deadline: " + maintenanceTask.getDeadline()));

        }
        else{
            redirectAttributes.addFlashAttribute("generationState", false);
        }

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());
        return REDIRECT_TO_MAINTENANCE_DETAILS;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/editIn")
    public  String renderEditMaintenanceTasksPage(@PathVariable Long householdId,
                                                 @PathVariable Long maintenanceId,
                                                 @PathVariable Long maintenanceTaskId,
                                                 Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE) && m1.getHousehold().equals(houseHold)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());

        MaintenanceTaskCreationDTO newMaintenanceTask1 = new MaintenanceTaskCreationDTO();

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(ASSIGNEE, membershipList);
        model.addAttribute(REPORTER, reporterList);
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
        model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, newMaintenanceTask1);
        return RETURN_EDIT_IN_MAINTENANCE_TASK;
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/editIn")
    public  String editMaintenanceTaskInPage(@PathVariable Long householdId,
                                       @PathVariable Long maintenanceId,
                                       @PathVariable Long maintenanceTaskId,
                                       @Valid @ModelAttribute("updatedMaintenanceTask") MaintenanceTaskCreationDTO updatedMaintenanceTask,
                                       BindingResult result,
                                       Model model){

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());


        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));
        MaintenanceTask maintenanceTask1 = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        try{
            if( result.hasErrors() || updatedMaintenanceTask.getDeadline() == null
                || updatedMaintenanceTask.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(ASSIGNEE, membershipList);
                model.addAttribute(REPORTER, reporterList);
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR, maintenance);
                model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask1);
                model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, updatedMaintenanceTask);
                model.addAttribute(REJECT_DEADLINE, REJECT_DEADLINE_MSG);
                return RETURN_EDIT_IN_MAINTENANCE_TASK;
            }
            String title = maintenanceTask1.getTitle();
            Date deadline = maintenanceTask1.getDeadline();
            String reporter = maintenanceTask1.getReporter().getUser().getUsername();
            String assignee = maintenanceTask1.getAssignee().getUser().getUsername();

            MaintenanceTask newMaintenanceTask = maintenanceTaskService.updateMaintenanceTask(maintenanceTaskId, updatedMaintenanceTask);
            maintenanceConfig.maintenanceEmailProcessing(maintenanceTask1.getAssignee(), maintenanceTask1.getReporter(), maintenanceTask1.getTitle(), "Maintenance Task Was Updated ", "was updated. ");


            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TASK_TITLE + maintenanceTask1.getTitle() + " updated. Before " + "title: " + title + ", reporter:- " + reporter + ", assignee:- " + assignee + ", deadline: " + " " + deadline + ". After title: " + newMaintenanceTask.getTitle() + ", reporter:- " + newMaintenanceTask.getReporter().getUser().getUsername() + ", assignee:- " + newMaintenanceTask.getAssignee().getUser().getUsername() + ", deadline: " + newMaintenanceTask.getDeadline()));
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask1);
            return "redirect:/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}";
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(ASSIGNEE, membershipList);
            model.addAttribute(REPORTER, reporterList);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask1);
            model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, updatedMaintenanceTask);
            model.addAttribute(REJECT_DEADLINE, REJECT_DEADLINE_MSG);
            return RETURN_EDIT_IN_MAINTENANCE_TASK;
        }
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/edit")
    public  String renderEditMaintenanceTaskPage(@PathVariable Long householdId,
                                                 @PathVariable Long maintenanceId,
                                                 @PathVariable Long maintenanceTaskId,
                                                 Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE) && m1.getHousehold().equals(houseHold)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());

        MaintenanceTaskCreationDTO newMaintenanceTask = new MaintenanceTaskCreationDTO();

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(ASSIGNEE, membershipList);
        model.addAttribute(REPORTER, reporterList);
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
        model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, newMaintenanceTask);
        model.addAttribute(REJECT_DEADLINE, null);
        return RETURN_EDIT_MAINTENANCE_TASK;
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/edit")
    public  String editMaintenanceTaskPage(@PathVariable Long householdId,
                                       @PathVariable Long maintenanceId,
                                       @PathVariable Long maintenanceTaskId,
                                       @Valid @ModelAttribute("updatedMaintenanceTask") MaintenanceTaskCreationDTO updatedMaintenanceTask,
                                       BindingResult result,
                                       Model model){

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        try{
            if( result.hasErrors() || updatedMaintenanceTask.getDeadline() == null
                || updatedMaintenanceTask.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(ASSIGNEE, membershipList);
                model.addAttribute(REPORTER, reporterList);
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR, maintenance);
                model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
                model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, updatedMaintenanceTask);
                model.addAttribute(REJECT_DEADLINE, REJECT_DEADLINE_MSG);
                return RETURN_EDIT_MAINTENANCE_TASK;
            }
            String title = maintenanceTask.getTitle();
            Date deadline = maintenanceTask.getDeadline();
            String reporter = maintenanceTask.getReporter().getUser().getUsername();
            String assignee = maintenanceTask.getAssignee().getUser().getUsername();


            MaintenanceTask newMaintenanceTask = maintenanceTaskService.updateMaintenanceTask(maintenanceTaskId, updatedMaintenanceTask);
            maintenanceConfig.maintenanceEmailProcessing(maintenanceTask.getAssignee(), maintenanceTask.getReporter(), maintenanceTask.getTitle(), "Maintenance Task Was Updated ", "was updated. ");

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TASK_TITLE + maintenanceTask.getTitle() + " updated. Before " + "title: " + title + ", reporter: " + reporter + ", assignee: " + assignee + ", deadline:" + " " + deadline + ". After title: " + newMaintenanceTask.getTitle() + ", reporter: " + newMaintenanceTask.getReporter().getUser().getUsername() + ", assignee: " + newMaintenanceTask.getAssignee().getUser().getUsername() + ", deadline:" + " " + newMaintenanceTask.getDeadline()));

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute("maintenanceId", maintenanceId);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());
            return REDIRECT_TO_MAINTENANCE_DETAILS;
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(ASSIGNEE, membershipList);
            model.addAttribute(REPORTER, reporterList);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
            model.addAttribute(NEW_MAINTENANCE_TASK_ATTR, updatedMaintenanceTask);
            model.addAttribute(REJECT_DEADLINE, REJECT_DEADLINE_MSG);
            return RETURN_EDIT_MAINTENANCE_TASK;
        }
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/delete")
    public String deleteMaintenanceTask(@PathVariable Long householdId,
                                              @PathVariable Long maintenanceId,
                                              @PathVariable Long maintenanceTaskId,
                                              Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));
        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        maintenanceTaskService.deleteMaintenanceTask(maintenanceTaskId);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TASK_TITLE + maintenanceTask.getTitle() + " deleted"));
        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute("maintenanceId", maintenanceId);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());
        return REDIRECT_TO_MAINTENANCE_DETAILS;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}")
    public  String renderMaintenanceTaskDetailsPage(@PathVariable Long householdId,
                                                    @PathVariable Long maintenanceId,
                                                    @PathVariable Long maintenanceTaskId,
                                                    Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));
        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));


        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
        return RETURN_MAINTENANCE_TASK_DETAILS;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/changeState")
    public  String renderChangeStatePage(@PathVariable Long householdId,
                                         @PathVariable Long maintenanceId,
                                         @PathVariable Long maintenanceTaskId,
                                         Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO();
        maintenanceStateDTO.setTaskState(maintenanceTask.getTaskState());
        maintenanceStateDTO.setTaskResolution(maintenanceTask.getTaskResolution());

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR,maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR,maintenanceTask);
        model.addAttribute(NEW_STATE_ATTR, maintenanceStateDTO);
        model.addAttribute(REJECT_MESSAGE, null);

        return "maintenance/edit/maintenanceTaskStateEdit";
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/changeState")
    public  String changeMaintenanceTaskState(@PathVariable Long householdId,
                                              @PathVariable Long maintenanceId,
                                              @PathVariable Long maintenanceTaskId,
                                              @Valid @ModelAttribute("updatedMaintenanceStateDTO") MaintenanceStateDTO updatedMaintenanceStateDTO,
                                              BindingResult result,
                                              Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask oldMaintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        try{

            if((!updatedMaintenanceStateDTO.getTaskResolution()
                && updatedMaintenanceStateDTO.getTaskState())
                || result.hasErrors())
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR,maintenance);
                model.addAttribute(MAINTENANCE_TASK_ATTR, oldMaintenanceTask);
                model.addAttribute(NEW_STATE_ATTR, updatedMaintenanceStateDTO);
                model.addAttribute(REJECT_MESSAGE, REJECT_MESSAGE_MSG);

                result.rejectValue("taskState", "error", REJECT_MESSAGE_MSG);
                return RETURN_STATE_EDIT_MAINTENANCE;
            }
            boolean prevState = oldMaintenanceTask.getTaskState();
            boolean prevResolution = oldMaintenanceTask.getTaskResolution();

            MaintenanceTask maintenanceTask = maintenanceTaskService.changeTaskState(updatedMaintenanceStateDTO, maintenanceTaskId);

            maintenanceConfig.maintenanceEmailProcessing(maintenanceTask.getAssignee(), maintenanceTask.getReporter(), maintenanceTask.getTitle(), "Maintenance Task Was Updated", "was updated");

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "State of maintenance task with title: " + oldMaintenanceTask.getTitle() + "was updated. Before " + "resolve: " + prevResolution + ", state: " + prevState + ". After resolution: " + maintenanceTask.getTaskResolution() + ", state: " + maintenanceTask.getTaskState()));

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
            return "redirect:/household/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}";
        }
        catch (RuntimeException e){
            result.rejectValue("taskState", "error", REJECT_MESSAGE_MSG);

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR,maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, oldMaintenanceTask);
            model.addAttribute(NEW_STATE_ATTR, updatedMaintenanceStateDTO);
            model.addAttribute(REJECT_MESSAGE, REJECT_MESSAGE_MSG);
            return "maintenance/edit/maintenanceTaskStateEdit";
        }
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/stop")
    public String closeMaintenanceTask(@PathVariable Long householdId,
                                             @PathVariable Long maintenanceId,
                                             @PathVariable Long maintenanceTaskId,
                                             Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));

        maintenanceTaskService.closeMaintenanceTask(maintenanceTask);
        maintenanceConfig.maintenanceEmailProcessing(maintenanceTask.getAssignee(), maintenanceTask.getReporter(), maintenanceTask.getTitle(), "Maintenance Task Was Updated", "was updated");

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TASK_TITLE + maintenanceTask.getTitle() + "was closed"));
        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());
        return REDIRECT_TO_MAINTENANCE_DETAILS;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/maintenanceTask/{maintenanceTaskId}/return")
    public ModelAndView returnToMaintenancePage(@PathVariable Long householdId,
                                                @PathVariable Long maintenanceId,
                                                @PathVariable Long maintenanceTaskId,
                                                Model model) {
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceTask maintenanceTask = maintenanceTaskService.findMaintenanceTaskById(maintenanceTaskId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceTaskId + DOES_NOT_EXIST));
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenanceTask);
        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + MAINTENANCE_ATTR + "/" + maintenanceId, (Map<String, ?>) model);
    }
}
