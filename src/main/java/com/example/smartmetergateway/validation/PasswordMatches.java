package com.example.smartmetergateway.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
Dient dazu, eingegebene Passwörter zu validieren.
Die Annotation bietet Standard-Fehlermeldungen sowie optionale Felder für Validierungsgruppen und zusätzliche Payloads, was sie in komplexeren Validierungsszenarien einsetzbar macht.
 */
@Target({TYPE, ANNOTATION_TYPE}) // Annotation kann auf Klassen und andere Annotationen angewendet werden
@Retention(RUNTIME) // Annotation wird zur Laufzeit verwendet
@Constraint(validatedBy = PasswordMatchesValidator.class) // Klasse, die die Validierung durchführt
@Documented
public @interface PasswordMatches {
    String message() default "Passwords do not match!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}