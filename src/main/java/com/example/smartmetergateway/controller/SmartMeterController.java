package com.example.smartmetergateway.controller;

import com.example.smartmetergateway.entities.Measurement;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.mapper.SmartMeterMapper;
import com.example.smartmetergateway.model.MeasurementDto;
import com.example.smartmetergateway.model.SmartMeterDto;
import com.example.smartmetergateway.repositiories.SmartMeterRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
        SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
        List<SmartMeter> smartMeters = smartMeterRepository.findByUser(smartMeterUser);
        List<SmartMeterDto> smartMeterDtos = smartMeters.stream().map(smartMeter -> {
            SmartMeterDto smartMeterDto = smartMeterMapper.toSmartMeterDto(smartMeter);
            smartMeterDto.setLastMeasurement(smartMeterMapper.toMeasurementDto(smartMeter.getMeasurements().getLast()));
            return smartMeterDto;
        }).toList();
        model.addAttribute("smartmeters", smartMeterDtos);
        return "smartmeters";
    }

    /*
    TODO: Smartmeter statisch erstellen und das hier löschen
    Neuen Smart Meter erstellen.
    Dafür wird der aktuell angemeldete Benutzer aus dem Authentication Objekt extrahiert und anhand des Benutzernamens aus der Datenbank geladen.
    Anschließend wird das SmartMeterDto Objekt in ein SmartMeter Entity Objekt gemappt und dem Benutzer zugeordnet.
    Das SmartMeter Entity Objekt wird in der Datenbank gespeichert und der Benutzer wird auf die SmartMeter Übersichtsseite weitergeleitet, wo der neu erstellte Smart Meter angezeigt wird.
     */
    @PostMapping("/smartmeters")
    public String postCreateSmartMeter(Authentication authentication, @ModelAttribute @Valid SmartMeterDto smartMeterDto) {
        User login = (User) authentication.getPrincipal();
        SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
        SmartMeter smartMeterEntity = smartMeterMapper.toSmartMeterEntity(smartMeterDto);
        smartMeterEntity.setUser(smartMeterUser);
        smartMeterRepository.save(smartMeterEntity);
        return "redirect:/smartmeters";
    }

    @GetMapping("/smartmeters/{id}")
    public String getSmartMeter(@PathVariable Long id, Authentication authentication, Model model) {
        User login = (User) authentication.getPrincipal();
        SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
        SmartMeter smartMeter = smartMeterRepository.findById(id).orElseThrow();
        if (!smartMeter.getUser().equals(smartMeterUser)) {
            return "403";
        }
        SmartMeterDto smartMeterDto = smartMeterMapper.toSmartMeterDto(smartMeter);

        model.addAttribute("smartmeter", smartMeterDto);
        model.addAttribute("newMeasurement", new MeasurementDto());
        return "smartmeter-detail";
    }

    @PostMapping("/smartmeters/{id}/measurements")
    public String postMeasurement(@PathVariable Long id, @ModelAttribute @Valid MeasurementDto measurementDto, Authentication authentication, Model model, HttpServletResponse httpServletResponse) {
        User login = (User) authentication.getPrincipal();
        SmartMeterUser smartMeterUser = userRepository.findByUsername(login.getUsername()).orElseThrow();
        SmartMeter smartMeter = smartMeterRepository.findById(id).orElseThrow();
        if (!smartMeter.getUser().equals(smartMeterUser)) {
            return "403";
        }
        List<Measurement> measurements = smartMeter.getMeasurements();
        if (measurements.getLast().getMeasurement().compareTo(measurementDto.getMeasurement()) > 0) {
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
