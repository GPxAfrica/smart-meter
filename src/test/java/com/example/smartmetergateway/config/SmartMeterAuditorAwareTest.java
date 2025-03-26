package com.example.smartmetergateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class SmartMeterAuditorAwareTest {

    private SmartMeterAuditorAware smartMeterAuditorAware = new SmartMeterAuditorAware();

    @Test
    void whenNotAuthReturnEmptyOptional() {
        assertThat(smartMeterAuditorAware.getCurrentAuditor()).isEmpty();
    }

    @WithMockUser(username = "user")
    @Test
    void whenAuthReturnUsername() {
        assertThat(smartMeterAuditorAware.getCurrentAuditor()).isEqualTo(Optional.of("user"));
    }

    @WithAnonymousUser
    @Test
    void whenAnonymousReturnEmptyOptional() {
        assertThat(smartMeterAuditorAware.getCurrentAuditor()).isEmpty();
    }

}