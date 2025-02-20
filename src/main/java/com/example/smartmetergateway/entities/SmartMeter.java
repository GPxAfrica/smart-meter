package com.example.smartmetergateway.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Repräsentiert ein intelligentes Messgerät (Smart Meter), das einem Benutzer zugeordnet ist.
@Entity
@EntityListeners(AuditingEntityListener.class) // TODO: bei allen Entitäten hinzufügen?? Fehlt bei Authority und SmartMeterUser
@Table(name = "smart_meters")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class SmartMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Erstellungsdatum und -uhrzeit des Messgeräts, automatisch über die AuditingEntityListener-Klasse gesetzt.
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "smartMeter") // Ein Messgerät kann mehrere Messwerte haben.
    @ToString.Exclude
    @OrderBy("createdAt ASC")
    private List<Measurement> measurements;

    @ManyToOne // Mehrere Messgeräte können einem Benutzer zugeordnet sein.
    private SmartMeterUser user;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SmartMeter that = (SmartMeter) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}