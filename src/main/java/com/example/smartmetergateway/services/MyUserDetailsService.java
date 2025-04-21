package com.example.smartmetergateway.services;

import com.example.smartmetergateway.config.props.SmartMeterOperator;
import com.example.smartmetergateway.entities.Authority;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.repositiories.AuthorityRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
Diese Klasse implementiert das Interface UserDetailsService, welches von Spring Security bereitgestellt wird.
Sie ermöglicht es, Benutzerinformationen zu laden und in ein UserDetails-Objekt zu konvertieren.
Dadurch kann Spring Security die Authentifizierung und Autorisierung von Benutzern durchführen.
 */
// Sorgt dafür, dass alle Methoden in dieser Klasse in einer Transaktion ausgeführt werden, was bei Datenbankoperationen wichtig ist.
@Transactional
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SmartMeterOperator smartMeterOperator;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @SuppressFBWarnings(value = {"UPM_UNCALLED_PRIVATE_METHOD", "DLS_DEAD_LOCAL_STORE"}, justification = "1: The method is called via reflection. 2: The return value is used to insert the roles once into the database")
    private void setupOperator() {
        if (smartMeterOperator.username() == null || smartMeterOperator.password() == null) {
            throw new IllegalArgumentException("SmartMeterOperator username and password must be set");
        }
        // Überprüfen, ob der SmartMeterOperator bereits in der Datenbank existiert
        Optional<SmartMeterUser> presentOperator = userRepository.findByUsername(smartMeterOperator.username());

        // Lege die Rolle ROLE_OPERATOR und ROLE_USER an, falls sie noch nicht existieren
        Authority operatorAuthority = authorityRepository.findById("ROLE_OPERATOR").orElseGet(() -> {
            Authority authority = new Authority().setAuthority("ROLE_OPERATOR").setSmartMeterUsers(new ArrayList<>());
            return authorityRepository.save(authority);
        });
        Authority userAuthority = authorityRepository.findById("ROLE_USER").orElseGet(() -> {
            Authority authority = new Authority().setAuthority("ROLE_USER").setSmartMeterUsers(new ArrayList<>());
            return authorityRepository.save(authority);
        });

        // Wenn der SmartMeterOperator nicht existiert, erstellen wir ihn
        if (presentOperator.isEmpty()) {
            SmartMeterUser operator = new SmartMeterUser();
            operator.setUsername(smartMeterOperator.username());
            operator.setPassword(passwordEncoder.encode(smartMeterOperator.password()));
            operator.setEnabled(true);
            operator.setAuthorities(Set.of(operatorAuthority));
            SmartMeterUser savedUser = userRepository.save(operator);
            operatorAuthority.getSmartMeterUsers().add(savedUser);
            authorityRepository.save(operatorAuthority);
        }
    }


    /*
    Diese Methode wird aufgerufen, wenn ein Benutzer sich anmeldet.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SmartMeterUser smartMeterUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                smartMeterUser.getUsername(),
                smartMeterUser.getPassword(),
                smartMeterUser.getEnabled(),
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                getAuthorities(smartMeterUser.getAuthorities())
        );
    }

    // Diese Methode konvertiert die Rollen des Benutzers in eine Liste von GrantedAuthority-Objekten, die von Spring Security benötigt wird.
    private static List<GrantedAuthority> getAuthorities(Set<Authority> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Authority role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        return authorities;
    }
}