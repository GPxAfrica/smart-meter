package com.example.smartmetergateway.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = -8006828369172971705L;

    private Long id;
    @Min(value = 0, message = "Measurement must be greater than or equal to 0.")
    @NotNull(message = "Measurement cannot be null.")
    private Long measurement;
    private LocalDateTime createdAt;
    private String createdBy;
}