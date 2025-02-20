package com.example.smartmetergateway.validation;

import com.example.smartmetergateway.model.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/*
Klasse wird verwendet, um zu überprüfen, ob das Passwort und die Bestätigung des Passworts übereinstimmen.
Dient als Validator für die Annotation @PasswordMatches.
Als Teil der Bean Validation integriert, wodurch die Validierung automatisch aufgerufen wird, wenn die Annotation @PasswordMatches verwendet wird.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getConfirmPassword());
    }
}