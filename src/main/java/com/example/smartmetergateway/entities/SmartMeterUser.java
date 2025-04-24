package com.example.smartmetergateway.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

// Repräsentiert einen Benutzer der Anwendung, der Smart Meter besitzen und verwalten kann.
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
@SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", justification = "Equals method specifically for hibernate proxies. Proxies differ from non proxy classes")
public class SmartMeterUser {

    @Id
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<SmartMeter> smartMeter = new ArrayList<>();

    @ManyToMany(mappedBy = "smartMeterUsers", cascade = {CascadeType.MERGE, CascadeType.PERSIST}) // Konfiguration der Beziehung in der Authority-Klasse.
    private Set<Authority> authorities = new LinkedHashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SmartMeterUser smartmeterUser = (SmartMeterUser) o;
        return getUsername() != null && Objects.equals(getUsername(), smartmeterUser.getUsername());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}