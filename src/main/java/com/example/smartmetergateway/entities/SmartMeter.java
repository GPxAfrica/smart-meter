package com.example.smartmetergateway.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Repräsentiert ein intelligentes Messgerät (Smart Meter), das einem Benutzer zugeordnet ist.
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "smart_meters")
@Getter
@Setter
@Accessors(chain = true)
@ToString
@RequiredArgsConstructor
@SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", justification = "Equals method specifically for hibernate proxies. Proxies differ from non proxy classes")
public class SmartMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long smartMeterId;

    // Erstellungsdatum und -uhrzeit des Messgeräts, automatisch über die AuditingEntityListener-Klasse gesetzt.
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "smartMeter", cascade = CascadeType.ALL) // Ein Messgerät kann mehrere Messwerte haben.
    @ToString.Exclude
    @OrderBy("createdAt ASC")
    private List<Measurement> measurements;

    @ManyToOne // Mehrere Messgeräte können einem Benutzer zugeordnet sein.
    @JoinColumn(name = "owner_username")
    private SmartMeterUser owner;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SmartMeter that = (SmartMeter) o;
        return getSmartMeterId() != null && Objects.equals(getSmartMeterId(), that.getSmartMeterId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}