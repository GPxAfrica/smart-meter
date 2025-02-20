package com.example.smartmetergateway.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

// Repräsentiert eine Rolle (Zugriffsrecht), die einem Benutzer zugewiesen werden kann.
@Getter
@Setter
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @Column(name = "authority", nullable = false, length = 50)
    private String authority;

    // Definition einer Many-to-Many-Beziehung zwischen Authority und SmartMeterUser.
    @NotNull
    @ManyToMany(fetch = FetchType.LAZY) // FetchType.LAZY: Die Benutzer werden nur geladen, wenn sie explizit abgefragt werden. Dadurch wird die Leistung verbessert.
    @OnDelete(action = OnDeleteAction.RESTRICT) // OnDeleteAction.RESTRICT: Wenn versucht wird, eine Rolle zu löschen, die einem Benutzer zugewiesen ist, wird eine Ausnahme ausgelöst.
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "authority", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "username"))
    private List<SmartMeterUser> smartMeterUsers;

    // Überschreibt die equals-Methode, um zwei Authority-Objekte zu vergleichen.
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

    // Überschreibt die hashCode-Methode, um den Hash-Code eines Authority-Objekts zu berechnen.
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}