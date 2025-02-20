package com.example.smartmetergateway.controller;

import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.mapper.SmartMeterMapper;
import com.example.smartmetergateway.model.SmartMeterDto;
import com.example.smartmetergateway.repositiories.SmartMeterRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

// Verwaltung der Endpunkte für die Smart Meter.
@Controller
public class SmartMeterController {

    private final UserRepository userRepository;
    private final SmartMeterRepository smartMeterRepository;
    private final SmartMeterMapper smartMeterMapper;

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
}
