package com.example.smartmetergateway.mapper;

import com.example.smartmetergateway.entities.Measurement;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.model.MeasurementDto;
import com.example.smartmetergateway.model.SmartMeterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SmartMeterMapper {

    @Mapping(target = "lastMeasurement", ignore = true)
    @Mapping(target = "owner", source = "owner.username")
    SmartMeterDto toSmartMeterDto(SmartMeter smartMeter);

    MeasurementDto toMeasurementDto(Measurement measurement);
}
