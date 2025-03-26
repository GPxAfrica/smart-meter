package com.example.smartmetergateway.controller;

import com.example.smartmetergateway.entities.Measurement;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.repositiories.SmartMeterRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest()
@ExtendWith(MockitoExtension.class)
class SmartMeterControllerTest {

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private SmartMeterRepository smartMeterRepository;
    @Autowired
    private MockMvc mockMvc;

    @WithMockUser("user")
    @Test
    void whenAuthenticatedUserGetsSmartmetersReturnTheirSmartmeters() throws Exception {
        SmartMeterUser smartMeterUser = new SmartMeterUser();
        when(userRepository.findByUsername(eq("user"))).thenReturn(Optional.of(smartMeterUser));
        SmartMeter smartMeter = new SmartMeter();
        smartMeter.setMeasurements(List.of(new Measurement()));
        when(smartMeterRepository.findByUser(smartMeterUser)).thenReturn(List.of(smartMeter));

        mockMvc.perform(get("/smartmeters").secure(true))
                .andExpect(status().isOk())
                .andExpect(view().name("smartmeters"))
                .andExpect(model().attributeExists("smartmeters"))
                .andExpect(model().attribute("smartmeters", hasSize(1)));
    }

    @WithMockUser("guest")
    @Test
    void userShouldNotSeeForeignSmartmeters() throws Exception {
        SmartMeterUser smartMeterUserGuest = new SmartMeterUser();
        when(userRepository.findByUsername(eq("guest"))).thenReturn(Optional.of(smartMeterUserGuest));

        SmartMeterUser smartMeterUserUser = new SmartMeterUser();
        when(userRepository.findByUsername(eq("user"))).thenReturn(Optional.of(smartMeterUserUser));
        SmartMeter smartMeter = new SmartMeter();
        smartMeter.setMeasurements(List.of(new Measurement()));
        when(smartMeterRepository.findByUser(smartMeterUserUser)).thenReturn(List.of(smartMeter));
        when(smartMeterRepository.findByUser(smartMeterUserGuest)).thenReturn(List.of());

        mockMvc.perform(get("/smartmeters").secure(true))
                .andExpect(status().isOk())
                .andExpect(view().name("smartmeters"))
                .andExpect(model().attributeExists("smartmeters"))
                .andExpect(model().attribute("smartmeters", hasSize(0)));
    }

    @WithAnonymousUser
    @Test
    void whenNotAuthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/smartmeters").secure(true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithMockUser("user")
    @Test
    void upgradeToSecureChannel() throws Exception {
        mockMvc.perform(get("/smartmeters").secure(false))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://**/smartmeters"));
    }

    @Test
    void postCreateSmartMeter() {

    }
}