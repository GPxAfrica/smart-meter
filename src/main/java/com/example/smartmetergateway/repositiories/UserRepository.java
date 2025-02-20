package com.example.smartmetergateway.repositiories;

import com.example.smartmetergateway.entities.SmartMeterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SmartMeterUser, String> {
    Optional<SmartMeterUser> findByUsername(String username);
}