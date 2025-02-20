package com.example.smartmetergateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.smartmetergateway.entities.Measurement}
 */
@Data
@NoArgsConstructor
public class MeasurementDto implements Serializable {
    private Long id;
    private Long measurement;
    private LocalDateTime createdAt;
    private User createdBy;
}