package com.example.smartmetergateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
Die Verwendung eines DTOs hilft dabei, die Domänenlogik von der Darstellungsebene zu trennen.
Dadurch werden Sicherheits- und Performanceaspekte verbessert, da nur die relevanten Daten übertragen werden.
 */
/**
 * DTO for {@link com.example.smartmetergateway.entities.Measurement}
 */
@Data
@NoArgsConstructor
public class MeasurementDto implements Serializable {
    private Long id;
    private Long measurement;
    private LocalDateTime createdAt;
    private String createdBy;
}