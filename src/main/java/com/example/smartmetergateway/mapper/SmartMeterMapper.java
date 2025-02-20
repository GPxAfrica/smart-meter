package com.example.smartmetergateway.mapper;

import com.example.smartmetergateway.entities.Measurement;
import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.model.MeasurementDto;
import com.example.smartmetergateway.model.SmartMeterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SmartMeterMapper {

//    @Mapping(target = "lastMeasurement", ignore = true)
SmartMeterDto toSmartMeterDto(SmartMeter smartMeter);

    MeasurementDto toMeasurementDto(Measurement measurement);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "measurements", ignore = true)
    SmartMeter toSmartMeterEntity(SmartMeterDto smartMeterDto);
}
