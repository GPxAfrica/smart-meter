package com.example.smartmetergateway.services;

import com.example.smartmetergateway.entities.Authority;
import com.example.smartmetergateway.entities.SmartMeterUser;
import com.example.smartmetergateway.repositiories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
Diese Klasse implementiert das Interface UserDetailsService, welches von Spring Security bereitgestellt wird.
Sie ermöglicht es, Benutzerinformationen zu laden und in ein UserDetails-Objekt zu konvertieren.
Dadurch kann Spring Security die Authentifizierung und Autorisierung von Benutzern durchführen.
 */
@Service
@Transactional // Sorgt dafür, dass alle Methoden in dieser Klasse in einer Transaktion ausgeführt werden, was bei Datenbankoperationen wichtig ist.
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    Diese Methode wird aufgerufen, wenn ein Benutzer sich anmeldet.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SmartMeterUser smartmeterUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                smartmeterUser.getUsername(),
                smartmeterUser.getPassword(),
                smartmeterUser.getEnabled(),
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                getAuthorities(smartmeterUser.getAuthorities())
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