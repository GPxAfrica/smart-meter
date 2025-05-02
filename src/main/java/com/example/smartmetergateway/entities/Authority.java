package com.example.smartmetergateway.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

// Repräsentiert eine Rolle (Zugriffsrecht), die einem Benutzer zugewiesen werden kann.
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "authorities")
@SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", justification = "Equals method specifically for hibernate proxies. Proxies differ from non proxy classes")
public class Authority {

    @Id
    @Column(name = "authority", nullable = false, length = 50)
    private String authority;

    // Definition einer Many-to-Many-Beziehung zwischen Authority und SmartMeterUser.
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    // FetchType.LAZY: Die Benutzer werden nur geladen, wenn sie explizit abgefragt werden. Dadurch wird die Leistung verbessert.
    @OnDelete(action = OnDeleteAction.RESTRICT)
    // OnDeleteAction.RESTRICT: Wenn versucht wird, eine Rolle zu löschen, die einem Benutzer zugewiesen ist, wird eine Ausnahme ausgelöst.
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "authority", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "username"))
    private List<SmartMeterUser> smartMeterUsers;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Authority authority1 = (Authority) o;
        return getAuthority() != null && Objects.equals(getAuthority(), authority1.getAuthority());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}