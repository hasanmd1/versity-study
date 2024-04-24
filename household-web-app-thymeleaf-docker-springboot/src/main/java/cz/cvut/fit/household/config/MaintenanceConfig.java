package cz.cvut.fit.household.config;

import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.service.interfaces.MaintenanceService;
import cz.cvut.fit.household.service.interfaces.MaintenanceTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@RequiredArgsConstructor
public class MaintenanceConfig {

    private final MaintenanceService maintenanceService;
    private final MaintenanceTaskService maintenanceTaskService;
    private final JavaMailSender mailSender;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void maintenanceExpirationJobs() {
        List<Maintenance> maintenanceList = maintenanceService.getAll();

        for(Maintenance maintenance: maintenanceList){

            if (!maintenance.getTaskState()
                && maintenance.getDeadline().before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())))
            {
                maintenanceEmailProcessing(maintenance.getAssignee(), maintenance.getReporter(), maintenance.getTitle(), "Maintenance Task Has Ended", "has ended");
            }
            else if(!maintenance.getTaskState()
                    && maintenance.getDeadline().before(Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant())))
            {
                maintenanceEmailProcessing(maintenance.getAssignee(), maintenance.getReporter(), maintenance.getTitle(), "Maintenance Will Soon End", "will end soon.");
            }
            maintenanceTaskGenerator(maintenance);
        }
    }

    private void maintenanceTaskGenerator(Maintenance maintenance){
        Long difference = maintenanceTaskService .calculateDifferent(maintenance.getStartDate(), maintenance.getDeadline());
        List<Date> possibleDates= maintenanceTaskService.getDates(maintenance.getStartDate(), maintenance.getEndDate(), maintenance.getFrequency(), maintenance.getFrequencyPeriod());
        List<Date> existingDates = maintenanceTaskService.getListOfMaintenanceTaskDates(maintenance);
        possibleDates.removeAll(existingDates);
        if (!maintenance.getTaskState() && !possibleDates.isEmpty()){
            for(Date dates: possibleDates){
                if(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDate().equals(dates.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())){
                    MaintenanceTask maintenanceTask = maintenanceTaskService.generateAutoTask(maintenance, dates, Date.from(dates.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(difference).atZone(ZoneId.systemDefault()).toInstant()));
                    maintenanceEmailProcessing(maintenanceTask.getAssignee(), maintenanceTask.getReporter(), maintenanceTask.getTitle(), "Maintenance Task Was Automatically Created", "was automatically created");
                    break;
                }
            }
        }
    }


    public void maintenanceEmailProcessing(Membership assignee, Membership reporter, String mainTitle, String title, String ending){
        if(assignee != null
            && reporter != null
            && title != null
            && ending != null)
        {
            if(assignee.equals(reporter)){
                sendMail(assignee.getUser().getEmail(), title, "Maintenance with title :" + " '" + mainTitle + "' " + ending);
            }
            else{
                sendMail(assignee.getUser().getEmail(), title, "Maintenance with title : " + "'" + mainTitle + "' " + ending);
                sendMail(reporter.getUser().getEmail(), title, "Maintenance with title : " + "'" + mainTitle + "' " + ending);
            }
        }
    }

    private void sendMail(String recipientAddress, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
