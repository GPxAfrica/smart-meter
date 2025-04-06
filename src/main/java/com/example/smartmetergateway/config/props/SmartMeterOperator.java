package com.example.smartmetergateway.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smartmeter.operator")
public record SmartMeterOperator(String username, String password) {
}
