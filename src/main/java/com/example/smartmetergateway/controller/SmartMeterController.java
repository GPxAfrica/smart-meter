package com.example.smartmetergateway.controller;

import com.example.smartmetergateway.entities.Measurement;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.mapper.SmartMeterMapper;
import com.example.smartmetergateway.model.MeasurementDto;
import com.example.smartmetergateway.model.SmartMeterDto;
import com.example.smartmetergateway.repositiories.SmartMeterRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

// Verwaltung der Endpunkte für die Smart Meter.
@Controller
public class SmartMeterController {

    private final UserRepository userRepository;
    private final SmartMeterRepository smartMeterRepository;
    private final SmartMeterMapper smartMeterMapper;
    public static final String ERR_LOWER_THAN_LAST = "New measurement cannot be lower than last measurement.";

    public SmartMeterController(UserRepository userRepository, SmartMeterRepository smartMeterRepository, SmartMeterMapper smartMeterMapper) {
        this.userRepository = userRepository;
        this.smartMeterRepository = smartMeterRepository;
        this.smartMeterMapper = smartMeterMapper;
    }

    /*
    Smart Meter des Benutzers abrufen.
    Dafür wird der aktuell angemeldete Benutzer aus dem Authentication Objekt extrahiert und anhand des Benutzernamens aus der Datenbank geladen.
    Anschließend werden die SmartMeter des Benutzers aus der Datenbank geladen und in SmartMeterDto Objekte gemappt, inklusive der letzten (aktuellsten) Messung.
     */
    @GetMapping("/smartmeters")
    public String getSmartMeters(Authentication authentication, Model model) {
        User login = (User) authentication.getPrincipal();
        List<SmartMeter> smartMeters;
        // Wenn der Benutzer ein Operator ist, werden alle SmartMeter geladen.
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATOR"))) {
            smartMeters = smartMeterRepository.findAll();
        } else { // Wenn der Benutzer kein Operator ist, werden nur die SmartMeter des Benutzers geladen.
            SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
            smartMeters = smartMeterUser.getSmartMeter();
        }
        List<SmartMeterDto> smartMeterDtos = smartMeters.stream().map(smartMeter -> {
            SmartMeterDto smartMeterDto = smartMeterMapper.toSmartMeterDto(smartMeter);
            if (!smartMeter.getMeasurements().isEmpty()) {
                smartMeterDto.setLastMeasurement(smartMeterMapper.toMeasurementDto(smartMeter.getMeasurements().getLast()));
            }
            return smartMeterDto;
        }).toList();
        model.addAttribute("smartmeters", smartMeterDtos);
        return "smartmeters";
    }

    /*
    Smart Meter Details inklusive Messständen abrufen.
    Dafür wird der SmartMeter anhand der ID aus der URL geladen und in ein SmartMeterDto Objekt gemappt.
    Anschließend wird überprüft, ob der Benutzer ein Operator ist oder ob der SmartMeter dem Benutzer gehört (Operator hat Zugriff auf alle SmartMeter).
    Wenn der SmartMeter dem Benutzer gehört, wird das SmartMeterDto Objekt an das Model übergeben und die Seite smartmeter-detail.html zurückgegeben.
    Wenn der Benutzer kein Operator ist und der SmartMeter nicht dem Benutzer gehört, wird ein 403 Fehler zurückgegeben.
     */
    @GetMapping("/smartmeters/{id}")
    public String getSmartMeter(@PathVariable Long id, Authentication authentication, Model model) {
        User login = (User) authentication.getPrincipal();
        SmartMeter smartMeter = smartMeterRepository.findById(id).orElseThrow();
        // Wenn der Benutzer kein Operator ist, wird überprüft, ob der SmartMeter dem Benutzer gehört.
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATOR"))) {
            SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
            if (!smartMeter.getOwner().equals(smartMeterUser)) {
                return "403";
            }
        }
        SmartMeterDto smartMeterDto = smartMeterMapper.toSmartMeterDto(smartMeter);

        model.addAttribute("smartmeter", smartMeterDto);
        model.addAttribute("newMeasurement", new MeasurementDto());
        return "smartmeter-detail";
    }

    /*
    Neuen Messwert eintragen.
    Dafür wird der SmartMeter anhand der ID aus der URL geladen.
    Anschließend wird überprüft, ob der Benutzer ein Operator ist oder ob der SmartMeter dem Benutzer gehört (nur diese beiden Akteure dürfen neue Werte eingeben).
    Zusätzlich findet eine Überprüfung statt, ob der neue Messwert kleiner ist als der vorherige, was zu einem Fehler führt.
    Falls kein Fehler auftritt, wird neuer Messwert erstellt, dem Smart Meter hinzugefügt und auf der Oberfläche angezeigt.
     */
    @PostMapping("/smartmeters/{id}/measurements")
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Null check is done in the if statement before accessing the fieldError's defaultMessage")
    public String postMeasurement(@PathVariable Long id, @ModelAttribute @Valid MeasurementDto measurementDto, BindingResult bindingResult, Authentication authentication, Model model, HttpServletResponse httpServletResponse) {
        SmartMeter smartMeter = smartMeterRepository.findById(id).orElseThrow();
        User login = (User) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATOR"))) {
            SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
            if (!smartMeter.getOwner().equals(smartMeterUser)) {
                return "403";
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("smartmeter", smartMeterMapper.toSmartMeterDto(smartMeter));
            model.addAttribute("newMeasurement", measurementDto);
            if (bindingResult.getFieldError() == null || bindingResult.getFieldError().getDefaultMessage() == null) {
                model.addAttribute("errorMessage", "Unknown error");
            } else {
                model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            }
            return "smartmeter-detail";
        }
        List<Measurement> measurements = smartMeter.getMeasurements();
        if (!measurements.isEmpty() && measurements.getLast().getMeasurement().compareTo(measurementDto.getMeasurement()) > 0) { // neuer Messwert muss größer sein als vorheriger
            model.addAttribute("smartmeter", smartMeterMapper.toSmartMeterDto(smartMeter));
            model.addAttribute("newMeasurement", measurementDto);
            model.addAttribute("errorMessage", ERR_LOWER_THAN_LAST);
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "smartmeter-detail";
        }
        Measurement newMeasurement = new Measurement();
        newMeasurement.setMeasurement(measurementDto.getMeasurement());
        newMeasurement.setSmartMeter(smartMeter);
        measurements.add(newMeasurement);
        smartMeterRepository.save(smartMeter);
        return "redirect:/smartmeters/" + id;
    }

}
