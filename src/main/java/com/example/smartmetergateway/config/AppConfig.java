package com.example.smartmetergateway.config;

import com.example.smartmetergateway.config.props.SmartMeterOperator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableConfigurationProperties(SmartMeterOperator.class)
// aktiviert das Auditing für JPA-Repositories, sodass die Auditing-Informationen automatisch in die Datenbank geschrieben werden
@EnableJpaAuditing
// aktiviert die JPA-Repositories und ermöglicht den Zugriff auf die Datenbank, ohne die Implementierung selbst schreiben zu müssen
@EnableJpaRepositories(basePackages = "com.example.smartmetergateway.repositiories")
public class AppConfig {

    @Bean
    public AuditorAware<String> auditorProvider() { // gibt eine Instanz des SmartMeterAuditorAware zurück, die den aktuellen Benutzer für das Auditing bereitstellt
        return new SmartMeterAuditorAware();
    }
}