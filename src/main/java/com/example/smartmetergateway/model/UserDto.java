package com.example.smartmetergateway.model;

import com.example.smartmetergateway.entities.SmartMeterUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link SmartMeterUser}
 */
@Data
@NoArgsConstructor
public class UserDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1318389307124649617L;

    // Username darf nicht leer sein und muss mindestens acht Zeichen lang sein.
    @Size(message = "Username must contain at least eight characters", min = 8)
    @NotBlank(message = "Username must be set")
    private String username;

    // Passwort darf nicht leer sein und muss mindestens acht Zeichen lang sein.
    @Size(message = "Password must contain at least eight characters", min = 8)
    @NotBlank(message = "Password must be set")
    private String password;

    // Passwort darf nicht leer sein und muss mindestens acht Zeichen lang sein.
    @Size(message = "Password must contain at least eight characters", min = 8)
    @NotBlank(message = "Password must be confirmed")
    private String confirmPassword;
}