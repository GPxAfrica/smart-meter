package com.example.smartmetergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

// Lädt die Konfigurationseigenschaften für die Sicherheit aus der Datei security.properties. Der Key muss auf den Ordner zeigen, in dem die Datei liegt.
@PropertySource(value = "file:${security.config.location}/security.properties", ignoreResourceNotFound = true)
@SpringBootApplication
public class SmartMeterGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMeterGatewayApplication.class, args);
    }

}
