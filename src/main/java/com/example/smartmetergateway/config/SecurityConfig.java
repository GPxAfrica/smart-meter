package com.example.smartmetergateway.config;

import com.example.smartmetergateway.config.props.SmartMeterOperator;
import com.example.smartmetergateway.repositiories.AuthorityRepository;
import com.example.smartmetergateway.repositiories.UserRepository;
import com.example.smartmetergateway.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${spring.web.security.debug:false}")
    boolean webSecurityDebug;

    // erstellt und konfiguriert eine SecurityFilterChain, die alle HTTP-Anfragen der Anwendung verarbeitet und die definierten Sicherheitsregeln anwendet
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        req -> req.requestMatchers("/registration").permitAll() // alle Requests auf "/registration" werden zugelassen
                                .requestMatchers("/").authenticated() // alle Requests auf "/" (index) erfordern eine Authentifizierung
                                .requestMatchers("/smartmeters/**").authenticated() // alle Requests auf "/smartmeters" und Subpfade erfordern eine Authentifizierung
                                .anyRequest().denyAll()) // deny by default für alle anderen Requests
                .formLogin(req -> req
                        .loginPage("/login").permitAll()) // das Login-Formular unter "/login" wird für alle freigegeben
                .logout(logout -> {
                    logout.permitAll(); // Logout ist für alle zugänglich
                    logout.logoutUrl("/logout"); // Logout-URL
                }) // auch der Logout ist für jeden zugänglich
                .requiresChannel(channel -> channel.anyRequest().requiresSecure()) // alle Requests erfordern eine sichere Verbindung
                .csrf(Customizer.withDefaults()) // nutzt SessionRepository um valide CSRF-Token abzugleichen (Thymeleaf sendet hidden input field "_csrf" mit dem Token)
                .build();
    }

    /*
    Erstellt einen UserDetailsService, der Benutzerinformationen aus einer Datenbanktabelle lädt.
    userDetailService stellt eine zentrale Komponente in Spring Security dar, die dafür verantwortlich ist, Benutzerinformationen (wie Benutzernamen, Passwort und Rollen) zu laden.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, SmartMeterOperator smartMeterOperator, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        return new MyUserDetailsService(userRepository, smartMeterOperator, authorityRepository, passwordEncoder);
    }

    // richtet den Authentifizierungsanbieter ein, der den UserDetailsService und den Argon2PasswordEncoder verwendet, um Anmeldedaten zu überprüfen.
    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService userDetailsService) {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Erstellung des Argon2PasswordEncoders mit spezifischen Parametern, um Passwörter sicher zu hashen und zu überprüfen.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3); // Durch die Nutzung eines Salts wird das Passwort vor Rainbow-Table-Angriffen geschützt
    }

    // Steuerung der Debug-Ausgabe von Spring Security
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }
}