package com.example.smartmetergateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.example.smartmetergateway.entities.SmartMeter}
 */
@Data
@NoArgsConstructor
public class SmartMeterDto implements Serializable {
    private Long id;
    private LocalDateTime createdAt;
    private List<MeasurementDto> measurements;
    private MeasurementDto lastMeasurement;
}