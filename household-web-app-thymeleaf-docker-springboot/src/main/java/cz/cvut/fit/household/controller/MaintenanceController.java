package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.config.MaintenanceConfig;
import cz.cvut.fit.household.datamodel.entity.*;
import cz.cvut.fit.household.datamodel.entity.events.OnInventoryChangeEvent;
import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceCreationDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.enums.MembershipRole;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.NonExistentEntityException;
import cz.cvut.fit.household.service.AuthorizationService;
import cz.cvut.fit.household.service.interfaces.HouseHoldService;
import cz.cvut.fit.household.service.interfaces.MaintenanceService;
import cz.cvut.fit.household.service.interfaces.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
public class MaintenanceController {
    private final HouseHoldService houseHoldService;
    private final MembershipService membershipService;
    private final MaintenanceService maintenanceService;
    private final MaintenanceConfig maintenanceConfig;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthorizationService authorizationService;

    private static final String MAINTENANCE_ATTR = "maintenance";
    private static final String NEW_MAINTENANCE_ATTR = "newMaintenance";
    private static final String NEW_STATE_ATTR = "newState";
    private static final String HOUSEHOLD_ATTR = "household";
    private static final String HOUSEHOLD_ID_ATTR = "householdId";
    private static final String HOUSEHOLD_WITH_ID = "Household With id: ";
    private static final String MAINTENANCE_WITH_ID = "Maintenance With id: ";
    private static final String DOES_NOT_EXIST = " doesn't exist";
    private static final String OWNER_PERMISSION = "permission";
    private static final String REJECT_FREQUENCY = "rejectFrequency";
    private static final String REJECT_DEADLINE = "rejectDeadline";
    private static final String REJECT_END_DATE = "rejectEndDate";
    private static final String REJECT_MESSAGE = "rejectMessage";
    private static final String ASSIGNEE = "assignee";
    private static final String REPORTER = "reporter";
    private static final String MAINTENANCE_TASK_ATTR = "maintenanceTask";
    private static final String MAINTENANCE_TITLE = "Maintenance with title: ";
    private static final String MAINTENANCE_UPDATED = "Maintenance with title: ";
    private static final String REDIRECTION_HOUSEHOLD = "redirect:/household/";
    private static final String RETURN_MAINTENANCE_DETAILS = "maintenance/maintenanceDetails";
    private static final String REDIRECT_TO_MAINTENANCE_VIEW = "redirect:/household/{householdId}/maintenance";
    private static final String RETURN_MAINTENANCE_VIEW = "maintenance/maintenanceView";
    private static final String RETURN_ADD_MAINTENANCE = "maintenance/addMaintenance";
    private static final String RETURN_EDIT_IN_MAINTENANCE = "maintenance/edit/maintenanceEditIn";
    private static final String RETURN_EDIT_MAINTENANCE = "maintenance/edit/maintenanceEdit";
    private static final String RETURN_STATE_EDIT_MAINTENANCE = "maintenance/edit/maintenanceStateEdit";
    private static final String REJECT_FREQUENCY_MSG = "Frequency must be a number greater than 0 and not be empty";
    private static final String REJECT_DEADLINE_MSG = "Deadline must not be a date before today and not be empty";
    private static final String REJECT_END_DATE_MSG = "End Date must not be a date before today and not be empty";
    private static final String REJECT_MESSAGE_MSG = "Task State cannot be set to close when it is not resolved yet";


    @GetMapping("/{householdId}/maintenance")
    public String renderMaintenancesPage(@PathVariable Long householdId, Model model) {
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, houseHold.getMaintenances());
        return RETURN_MAINTENANCE_VIEW;
    }

    @GetMapping("/{householdId}/maintenance/add")
    public  String renderAddMaintenancePage(@PathVariable Long householdId,
                                            Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE) && m1.getHousehold().equals(houseHold)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());

        MaintenanceCreationDTO maintenanceCreationDTO = new MaintenanceCreationDTO();

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR,houseHold);
        model.addAttribute(ASSIGNEE, membershipList);
        model.addAttribute(REPORTER, reporterList);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenanceCreationDTO);
        return RETURN_ADD_MAINTENANCE;
    }

    @PostMapping("/{householdId}/maintenance")
    public  String getAddMaintenancePage(@PathVariable Long householdId,
                                      @Valid @ModelAttribute("updatedMaintenance") MaintenanceCreationDTO updatedMaintenance,
                                      BindingResult result,
                                      Model model){

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());


        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        try{
            if (result.hasErrors()
                || (updatedMaintenance.getEndDate() == null || updatedMaintenance.getEndDate().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || (updatedMaintenance.getDeadline() == null || updatedMaintenance.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || updatedMaintenance.getFrequency() <= 0L)
            {
                model.addAttribute(HOUSEHOLD_ATTR,houseHold);
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(ASSIGNEE, membershipList);
                model.addAttribute(REPORTER, reporterList);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR, updatedMaintenance);
                setAddMaintenancePage(updatedMaintenance, model);
                return RETURN_ADD_MAINTENANCE;

            }

            Maintenance maintenance = maintenanceService.addMaintenance(updatedMaintenance, houseHold, updatedMaintenance.getReporter(), updatedMaintenance.getAssignee());
            maintenanceConfig.maintenanceEmailProcessing(maintenance.getAssignee(), maintenance.getReporter(), maintenance.getTitle(), "Maintenance Created", "created");


            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TITLE + maintenance.getTitle() + " created. Reporter:- " + maintenance.getReporter().getUser().getUsername() + ", assignee:- " + maintenance.getAssignee().getUser().getUsername() + ", frequency:- " + maintenance.getFrequency() + " " + maintenance.getFrequencyPeriod().getFrequencyPeriod() + ", deadline:- " + maintenance.getDeadline() + ", end date:- " + maintenance.getEndDate()));
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, houseHold.getMaintenances());
            return REDIRECT_TO_MAINTENANCE_VIEW;
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR,houseHold);
            model.addAttribute(ASSIGNEE, membershipList);
            model.addAttribute(REPORTER, reporterList);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, updatedMaintenance);
            setAddMaintenancePage(updatedMaintenance, model);
            return RETURN_ADD_MAINTENANCE;
        }
    }

    public void setEditingPage(Long householdId, Long maintenanceId, Model model){
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE) && m1.getHousehold().equals(houseHold)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(ASSIGNEE, membershipList);
        model.addAttribute(REPORTER, reporterList);
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/editIn")
    public  String renderEditMaintenanceInPage(@PathVariable Long householdId,
                                             @PathVariable Long maintenanceId,
                                             Model model){
        setEditingPage(householdId, maintenanceId, model);
        MaintenanceCreationDTO newMaintenance = new MaintenanceCreationDTO();
        model.addAttribute(NEW_MAINTENANCE_ATTR, newMaintenance);
        return RETURN_EDIT_IN_MAINTENANCE;
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/editIn")
    public  String editMaintenanceInPage(@PathVariable Long householdId,
                                       @PathVariable Long maintenanceId,
                                       @Valid @ModelAttribute("updatedMaintenance") MaintenanceCreationDTO updatedMaintenance,
                                       BindingResult result,
                                       Model model){

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());


        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance1 = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        try{
            if (result.hasErrors()
                || (updatedMaintenance.getEndDate() == null || updatedMaintenance.getEndDate().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || (updatedMaintenance.getDeadline() == null || updatedMaintenance.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || updatedMaintenance.getFrequency() <= 0L)
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(ASSIGNEE, membershipList);
                model.addAttribute(REPORTER, reporterList);
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR, maintenance1);
                model.addAttribute(NEW_MAINTENANCE_ATTR, updatedMaintenance);
                setAddMaintenancePage(updatedMaintenance, model);

                return RETURN_EDIT_IN_MAINTENANCE;
            }
            String title = maintenance1.getTitle();
            Date deadline = maintenance1.getDeadline();
            Date endDate = maintenance1.getEndDate();
            String assignee = maintenance1.getAssignee().getUser().getUsername();
            String reporter = maintenance1.getReporter().getUser().getUsername();
            Long frequency = maintenance1.getFrequency();
            String fPeriod = maintenance1.getFrequencyPeriod().getFrequencyPeriod();

            Maintenance newMaintenance = maintenanceService.updateMaintenance(maintenanceId, updatedMaintenance, houseHold, updatedMaintenance.getReporter(), updatedMaintenance.getAssignee());
            maintenanceConfig.maintenanceEmailProcessing(maintenance1.getAssignee(), maintenance1.getReporter(), maintenance1.getTitle(), MAINTENANCE_UPDATED, "was updated.");

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TITLE + maintenance1.getTitle() + " updated. Before" + " title: " + title + ", reporter:- " + reporter + ", assignee:- " + assignee + ", frequency:- " + frequency + fPeriod + ", deadline: " + deadline + ", endDate: " + endDate + ". After title: " + newMaintenance.getTitle() + ", reporter : " + newMaintenance.getReporter().getUser().getUsername() + ", assignee : " + newMaintenance.getAssignee().getUser().getUsername() + ", frequency : " + newMaintenance.getFrequency() + newMaintenance.getFrequencyPeriod().getFrequencyPeriod() + ", deadline:" + newMaintenance.getDeadline() + ", endDate" + newMaintenance.getEndDate()));

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance1);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance1.getMaintenanceTasks());
            return "redirect:/household/{householdId}/maintenance/{maintenanceId}";
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(ASSIGNEE, membershipList);
            model.addAttribute(REPORTER, reporterList);
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance1);
            model.addAttribute(NEW_MAINTENANCE_ATTR, updatedMaintenance);
            setAddMaintenancePage(updatedMaintenance, model);
            return RETURN_EDIT_IN_MAINTENANCE;
        }
    }

    public void setAddMaintenancePage(MaintenanceCreationDTO updatedMaintenance, Model model){

        if(updatedMaintenance.getFrequency() <= 0L){
            model.addAttribute(REJECT_FREQUENCY, REJECT_FREQUENCY_MSG);
        }
        else{
            model.addAttribute(REJECT_FREQUENCY, null);
        }
        if(updatedMaintenance.getDeadline() == null || updatedMaintenance.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))){
            model.addAttribute(REJECT_DEADLINE, REJECT_DEADLINE_MSG);
        }
        else{
            model.addAttribute(REJECT_DEADLINE, null);
        }
        if(updatedMaintenance.getEndDate() == null || updatedMaintenance.getEndDate().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))){
            model.addAttribute(REJECT_END_DATE, REJECT_END_DATE_MSG);
        }
        else{
            model.addAttribute(REJECT_END_DATE, null);
        }
    }


    @GetMapping("/{householdId}/maintenance/{maintenanceId}/edit")
    public  String renderEditMaintenancePage(@PathVariable Long householdId,
                                             @PathVariable Long maintenanceId,
                                             Model model){

        setEditingPage(householdId, maintenanceId, model);
        MaintenanceCreationDTO newMaintenance1 = new MaintenanceCreationDTO();
        model.addAttribute(NEW_MAINTENANCE_ATTR, newMaintenance1);

        return RETURN_EDIT_MAINTENANCE;
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/edit")
    public  String getEditMaintenancePage(@PathVariable Long householdId,
                                      @PathVariable Long maintenanceId,
                                      @Valid @ModelAttribute("updatedMaintenance") MaintenanceCreationDTO updatedMaintenance,
                                      BindingResult result,
                                      Model model){

        List<Membership> membershipList = membershipService.findAllMemberships().stream().filter(m1 -> m1.getStatus().equals(MembershipStatus.ACTIVE)).collect(Collectors.toList());
        List<Membership> reporterList = membershipList.stream().filter(m1 -> m1.getMembershipRole().equals(MembershipRole.OWNER)).collect(Collectors.toList());


        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));
        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        try{
            if( result.hasErrors()
                || (updatedMaintenance.getEndDate() == null || updatedMaintenance.getEndDate().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || (updatedMaintenance.getDeadline() == null || updatedMaintenance.getDeadline().before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())))
                || updatedMaintenance.getFrequency() <= 0L)
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(ASSIGNEE, membershipList);
                model.addAttribute(REPORTER, reporterList);
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR, maintenance);
                model.addAttribute(NEW_MAINTENANCE_ATTR, updatedMaintenance);
                setAddMaintenancePage(updatedMaintenance, model);
                return RETURN_EDIT_MAINTENANCE;
            }
            String title = maintenance.getTitle();
            Date deadline = maintenance.getDeadline();
            Date endDate = maintenance.getEndDate();
            String assignee = maintenance.getAssignee().getUser().getUsername();
            String reporter = maintenance.getReporter().getUser().getUsername();
            Long frequency = maintenance.getFrequency();
            String fPeriod = maintenance.getFrequencyPeriod().getFrequencyPeriod();

            Maintenance newMaintenance = maintenanceService.updateMaintenance(maintenanceId, updatedMaintenance, houseHold, updatedMaintenance.getReporter(), updatedMaintenance.getAssignee());
            maintenanceConfig.maintenanceEmailProcessing(maintenance.getAssignee(), maintenance.getReporter(), maintenance.getTitle(), "Maintenance Was Updated", "was updated");

            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TITLE + maintenance.getTitle() + " updated. Before" + " title: " + title + ", reporter: " + reporter + ", assignee: " + assignee + ", frequency: " + frequency + fPeriod + ", deadline: " + deadline + ", endDate: " + endDate + ". After title: " + newMaintenance.getTitle() + ", reporter: " + newMaintenance.getReporter().getUser().getUsername() + ", assignee: " + newMaintenance.getAssignee().getUser().getUsername() + ", frequency: " + newMaintenance.getFrequency() + newMaintenance.getFrequencyPeriod().getFrequencyPeriod() + ", deadline:" + newMaintenance.getDeadline() + ", endDate" + newMaintenance.getEndDate()));

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, houseHold.getMaintenances());
            return REDIRECT_TO_MAINTENANCE_VIEW;
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(ASSIGNEE, membershipList);
            model.addAttribute(REPORTER, reporterList);
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(NEW_MAINTENANCE_ATTR, updatedMaintenance);
            setAddMaintenancePage(updatedMaintenance, model);
            return RETURN_EDIT_MAINTENANCE;
        }
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/delete")
    public  String deleteMaintenance(@PathVariable Long householdId,
                                     @PathVariable Long maintenanceId,
                                     Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        maintenanceService.deleteMaintenance(maintenanceId);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, MAINTENANCE_TITLE + maintenance.getTitle() + " deleted"));
        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, houseHold.getMaintenances());
        return REDIRECT_TO_MAINTENANCE_VIEW;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}")
    public  String renderMaintenanceDetailsPage(@PathVariable Long householdId,
                                                @PathVariable Long maintenanceId,
                                                Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));


        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());

        Boolean generationState = (Boolean) model.asMap().get("generationState");
        if (generationState != null) {
            model.addAttribute("generationState", generationState);
        }

        return RETURN_MAINTENANCE_DETAILS;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/changeState")
    public  String renderChangeStatePage(@PathVariable Long householdId,
                                     @PathVariable Long maintenanceId,
                                     Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        MaintenanceStateDTO maintenanceStateDTO = new MaintenanceStateDTO();
        maintenanceStateDTO.setTaskState(maintenance.getTaskState());
        maintenanceStateDTO.setTaskResolution(maintenance.getTaskResolution());

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR,maintenance);
        model.addAttribute(NEW_STATE_ATTR, maintenanceStateDTO);
        model.addAttribute(REJECT_MESSAGE, null);
        return RETURN_STATE_EDIT_MAINTENANCE;
    }

    @PostMapping("/{householdId}/maintenance/{maintenanceId}/changeState")
    public  String getChangeMaintenanceState(@PathVariable Long householdId,
                                          @PathVariable Long maintenanceId,
                                          @Valid @ModelAttribute("updatedMaintenanceStateDTO") MaintenanceStateDTO updatedMaintenanceStateDTO,
                                          BindingResult result,
                                          Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance oldmaintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        try {
            if((!updatedMaintenanceStateDTO.getTaskResolution()
                && updatedMaintenanceStateDTO.getTaskState())
                || result.hasErrors())
            {
                model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
                model.addAttribute(HOUSEHOLD_ATTR, houseHold);
                model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
                model.addAttribute(MAINTENANCE_ATTR,oldmaintenance);
                model.addAttribute(NEW_STATE_ATTR, updatedMaintenanceStateDTO);
                model.addAttribute(REJECT_MESSAGE, REJECT_MESSAGE_MSG);

                result.rejectValue("taskState", "error", REJECT_MESSAGE_MSG);
                return RETURN_STATE_EDIT_MAINTENANCE;
            }
            String prevResolution = String.valueOf(oldmaintenance.getTaskResolution());
            String prevState = String.valueOf(oldmaintenance.getTaskState());

            Maintenance maintenance = maintenanceService.changeState(updatedMaintenanceStateDTO, maintenanceId);

            maintenanceConfig.maintenanceEmailProcessing(maintenance.getAssignee(), maintenance.getReporter(), maintenance.getTitle(), "Maintenance Was Updated", "was updated");
            eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "State of maintenance with title: " + oldmaintenance.getTitle() + " was updated. Before " + "resolve: " + prevResolution + ", state: " + prevState + ". After resolution: " + maintenance.getTaskResolution() + ", state: " + maintenance.getTaskState()));

            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR, maintenance);
            model.addAttribute(MAINTENANCE_TASK_ATTR, maintenance.getMaintenanceTasks());
            return "redirect:/household/{householdId}/maintenance/{maintenanceId}";
        }
        catch (RuntimeException e){
            model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
            model.addAttribute(HOUSEHOLD_ATTR, houseHold);
            model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
            model.addAttribute(MAINTENANCE_ATTR,oldmaintenance);
            model.addAttribute(NEW_STATE_ATTR, updatedMaintenanceStateDTO);
            model.addAttribute(REJECT_MESSAGE, REJECT_MESSAGE_MSG);

            result.rejectValue("taskState", "error", "Fields should be properly defined");
            return RETURN_STATE_EDIT_MAINTENANCE;
        }
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/stop")
    public  String stopGeneratingMaintenanceTask(@PathVariable Long householdId,
                                     @PathVariable Long maintenanceId,
                                     Model model){

        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));

        boolean prevState = maintenance.getTaskState();

        Maintenance newMaintenance = maintenanceService.stopGeneratingMaintenance(maintenance);

        eventPublisher.publishEvent(new OnInventoryChangeEvent(householdId, "State of maintenance with title: " + maintenance.getTitle() + " was updated. Before generation state: " + prevState + ". After generation state: " + newMaintenance.getTaskState()));
        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(HOUSEHOLD_ATTR, houseHold);
        model.addAttribute(HOUSEHOLD_ID_ATTR, householdId);
        model.addAttribute(MAINTENANCE_ATTR, houseHold.getMaintenances());
        return REDIRECT_TO_MAINTENANCE_VIEW;
    }

    @GetMapping("/{householdId}/maintenance/{maintenanceId}/return")
    public ModelAndView returnToMaintenance(@PathVariable Long householdId,
                                             @PathVariable Long maintenanceId,
                                             Model model) {

        Maintenance maintenance = maintenanceService.findMaintenanceById(maintenanceId)
                .orElseThrow(() -> new NonExistentEntityException(MAINTENANCE_WITH_ID + maintenanceId + DOES_NOT_EXIST));
        Household houseHold = houseHoldService.findHouseHoldById(householdId)
                .orElseThrow(() -> new NonExistentEntityException(HOUSEHOLD_WITH_ID + householdId + DOES_NOT_EXIST));

        model.addAttribute(OWNER_PERMISSION, authorizationService.isOwner(houseHold));
        model.addAttribute(MAINTENANCE_ATTR, maintenance);
        return new ModelAndView(REDIRECTION_HOUSEHOLD + householdId + "/" + MAINTENANCE_ATTR, (Map<String, ?>) model);
    }
}
