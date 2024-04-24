/*
 * (c) copyright 2012-2022 mgm technology partners GmbH.
 * This software, the underlying source code and other artifacts are protected by copyright.
 * All rights, in particular the right to use, reproduce, publish and edit are reserved.
 * A simple right of use (license) can be acquired for use, duplication, publication, editing etc..
 * Requests for this can be made at A12-license@mgm-tp.com or other official channels of the copyright holder.
 */
package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/household")
@RequiredArgsConstructor
public class LogsController {

    private final LogRepository logRepository;

    @GetMapping("/{householdId}/logs")
    public String renderLogsPage(@PathVariable Long householdId,
                                 Model model) {
        model.addAttribute("householdId", householdId);
        model.addAttribute("logs", logRepository.findByHouseholdIdOrderByIdDesc(householdId));
        return "logs";
    }

    @GetMapping("/{householdId}/logs/delete")
    @Transactional
    public RedirectView deleteAllLogs(@PathVariable Long householdId) {
        logRepository.deleteAllByHouseholdId(householdId);
        return new RedirectView("/household/" + householdId + "/logs");
    }
}
