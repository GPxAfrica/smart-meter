package com.example.smartmetergateway.model;

import com.example.smartmetergateway.entities.SmartMeterUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link SmartMeterUser}
 */
@Data
@NoArgsConstructor
public class UserDto implements Serializable {

    @Size(message = "Username must contain at least eight characters", min = 8)
    @NotBlank(message = "Username must be set")
    private String username;

    @Size(message = "Password must contain at least eight characters", min = 8)
    @NotBlank(message = "Password must be set")
    private String password;

    @Size(message = "Password must contain at least eight characters", min = 8)
    @NotBlank(message = "Password must be set")
    private String confirmPassword;
}