package com.example.smartmetergateway.repositiories;

import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmartMeterRepository extends JpaRepository<SmartMeter, Long> {
    List<SmartMeter> findByUser(SmartMeterUser user);
}
