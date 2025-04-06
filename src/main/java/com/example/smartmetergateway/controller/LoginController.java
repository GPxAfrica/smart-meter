package com.example.smartmetergateway.controller;

import com.example.smartmetergateway.entities.Authority;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.model.UserDto;
import com.example.smartmetergateway.repositiories.AuthorityRepository;
import com.example.smartmetergateway.repositiories.SmartMeterRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// Verwaltung der Login- und Registrierungsfunktionen
@Controller
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    // Bei GET Anfragen auf "/login", wird die login.html Seite zurückgegeben
    @GetMapping("/login")
    String login() {
        return "login";
    }

    // Bei GET Anfragen auf "/registration", wird die registration.html Seite zurückgegeben
    @GetMapping("/registration")
    String getRegistration(Model model) {
        model.addAttribute("user", new UserDto()); // UserDto ist ein Model, welches die Daten des Benutzers enthält, die bei der Registrierung eingegeben werden müssen
        return "registration";
    }

    /*
    Verarbeitung der POST Anfragen auf "/registration", um einen neuen Benutzer zu registrieren.
    Die Benutzereingaben werden validiert und wenn die Validierung fehlschlägt, wird die registration.html Seite erneut zurückgegeben, um den Registrierungsprozess erneut zu starten.
    Die Validierung findet in der UserDto Klasse statt, die die Validierungskriterien für die Benutzereingaben enthält.
    Wenn die Validierung erfolgreich ist, wird ein neuer Benutzer erstellt und in der Datenbank gespeichert, sofern der Benutzer noch nicht existiert.
    Das eingegebene Passwort wird mit einem Salt versehen und gehasht gespeichert, wodurch das Passwort vor Rainbow-Table-Angriffen geschützt wird.
    Anschließend wird der Benutzer auf die Login-Seite weitergeleitet inklusive Erfolgsmeldung.
     */
    @PostMapping("/registration")
    String postRegistration(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "registration";
        }
        Optional<SmartMeterUser> userEntity = userRepository.findByUsername(user.getUsername());
        if (userEntity.isEmpty()) {
            SmartMeterUser newSmartMeterUser = new SmartMeterUser();
            newSmartMeterUser.setUsername(user.getUsername());
            newSmartMeterUser.setPassword(passwordEncoder.encode(user.getPassword())); // Hier wird das Passwort gehasht und gesetzt
            // hier könnte noch ein MFA Token geprüft werden, aber aus Gründen der Einfachheit wird dieses weggelassen
            newSmartMeterUser.setEnabled(true);

            Authority userAuthority = authorityRepository.findById("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            newSmartMeterUser.getAuthorities().add(userAuthority);
            newSmartMeterUser.getSmartMeter().add(new SmartMeter().setOwner(newSmartMeterUser));
            userRepository.save(newSmartMeterUser);
            model.addAttribute("message", "Registration successful! You can now log in.");
            return "login";
        } else {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            model.addAttribute("user", user);
            return "registration";
        }
    }
}