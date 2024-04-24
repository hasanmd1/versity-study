package cz.cvut.fit.household.controller;

import cz.cvut.fit.household.datamodel.entity.Membership;
import cz.cvut.fit.household.datamodel.entity.events.OnForgotPasswordEvent;
import cz.cvut.fit.household.datamodel.entity.events.OnRegistrationCompleteEvent;
import cz.cvut.fit.household.datamodel.entity.user.User;
import cz.cvut.fit.household.datamodel.entity.user.UserRegistrationDTO;
import cz.cvut.fit.household.datamodel.entity.user.VerificationToken;
import cz.cvut.fit.household.datamodel.enums.MembershipStatus;
import cz.cvut.fit.household.exception.VerificationTokenException;
import cz.cvut.fit.household.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final String ERROR = "error";
    private static final String NO_VERIFICATION_TOKEN = "No verification token";

    @GetMapping("/")
    public String defaultPage() {
        return "redirect:/welcome";
    }

    @GetMapping("/login")
    public String renderLoginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String renderLogoutPage(){
        return "redirect:/login?logout";
    }

    @GetMapping("/signup")
    public String renderSignUpPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "signUp";
    }

    @GetMapping("/forgotPassword")
    public String renderForgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        try {
            eventPublisher.publishEvent(new OnForgotPasswordEvent(email));
            return "resetPwdEmailSent";
        } catch (Exception exception) {
            model.addAttribute("wrongEmail", "true");
            return "forgotPassword";
        }
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute("user") UserRegistrationDTO userRegistrationDTO, BindingResult result, HttpServletRequest httpServletRequest, Model model) {
        User user = userRegistrationDTO.getUser();

        if (userService.findUserByUsername(user.getUsername()).isPresent()) {
            user.setUsername("");
            result.rejectValue("user.username", ERROR, "Username already exists");
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            user.setEmail("");
            result.rejectValue("user.email", ERROR, "Email already exists");
        }

        if (!user.getPassword().equals(userRegistrationDTO.getPasswordConfirmation())) {
            user.setPassword("");
            result.rejectValue("user.password", ERROR, "Passwords should match");
        }

        if (!result.getAllErrors().isEmpty()) {
            return "signUp";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createOrUpdateUser(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(httpServletRequest.getLocale(), user));
        return "sign-up-successful";
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest webRequest, Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            throw new VerificationTokenException(NO_VERIFICATION_TOKEN);
        }

        User user = verificationToken.getUser();
        if (verificationToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new VerificationTokenException(NO_VERIFICATION_TOKEN);
        }

        user.setEnabled(true);
        userService.createOrUpdateUser(user);
        return "registrationConfirm";
    }

    @GetMapping("/passwordRecovery")
    public String renderRecoverPassword(@ModelAttribute String password, Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            throw new VerificationTokenException(NO_VERIFICATION_TOKEN);
        }

        User user = verificationToken.getUser();
        if (verificationToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new VerificationTokenException("Verification token expired");
        }

        model.addAttribute("user", user);
        model.addAttribute("token", token);
        return "recoverPassword";
    }

    @PostMapping("/passwordRecovery/{token}")
    public String recoverPassword(@PathVariable String token, @RequestParam("password") String password,
                                  @RequestParam("passwordConfirmation") String passwordConfirmation, Model model) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            throw new VerificationTokenException(NO_VERIFICATION_TOKEN);
        }

        if (!password.equals(passwordConfirmation)) {
            model.addAttribute("nonMatchingPasswords", true);
            return "recoverPassword";
        }

        User user = verificationToken.getUser();
        if (verificationToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new VerificationTokenException("Verification token expired");
        }


        user.setPassword(passwordEncoder.encode(password));
        userService.createOrUpdateUser(user);
        return "login";
    }

    @GetMapping("/welcome")
    public String renderWelcomePage(Authentication authentication, Model model) {

        User user = userService.findUserByUsername(authentication.getName())
                .orElseThrow(RuntimeException::new);

        List<Membership> pendingMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.PENDING))
                .collect(Collectors.toList());

        List<Membership> activeMemberships =  user.getMemberships()
                .stream().filter(membership -> membership.getStatus().equals(MembershipStatus.ACTIVE))
                .collect(Collectors.toList());

        model.addAttribute("pendingHouseholds", pendingMemberships);
        model.addAttribute("activeHouseholds", activeMemberships);
        return "welcome";
    }
}
