package com.example.smartmetergateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.example.smartmetergateway.entities.SmartMeter}
 */
@Data
@NoArgsConstructor
public class SmartMeterDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -9020882333419841517L;
    private Long smartMeterId;
    private LocalDateTime createdAt;
    private List<MeasurementDto> measurements;
    private MeasurementDto lastMeasurement;
    private String owner;
}