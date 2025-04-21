package com.example.smartmetergateway.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class SmartMeterAuditorAware implements AuditorAware<String> { // Integration der Klasse in den Auditing-Mechanismus von Spring Data JPA

    /*
    Gibt den Benutzernamen des aktuellen Benutzers zurück, sofern dieser authentifiziert ist, andernfalls wird ein leerer Optional zurückgegeben.
    Über das Authentication-Objekt wird der aktuelle Benutzer aus dem SecurityContext extrahiert und der Benutzername zurückgegeben.
     */
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Optional<Object> principal = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal);
        try {
            return principal
                    .map(User.class::cast)
                    .map(User::getUsername);
        } catch (
                ClassCastException e) { // Wenn der Typ der Authentifizierung nicht User ist, wird ein leerer Optional zurückgegeben.
            return Optional.empty();
        }
    }
}
