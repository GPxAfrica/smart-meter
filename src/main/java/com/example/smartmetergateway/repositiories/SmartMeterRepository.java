package com.example.smartmetergateway.repositiories;

import com.example.smartmetergateway.entities.SmartMeter;
import com.example.smartmetergateway.entities.SmartMeterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
Ermöglicht den Zugriff auf Smart Meter Entitäten in der Datenbank.
Erweitert JpaRepository, um Standardfunktionen wie das Speichern, Löschen und Suchen von Entitäten zu ermöglichen.
Der Vorteil dieser Erweiterung ist, dass bereits viele Standardfunktionen bereitgestellt werden, die nicht explizit selbst implementiert werden müssen.
Dazu zählt beispielsweise das Suchen von Entitäten anhand von Attributen, oder die CRUD-Operationen.
 */
public interface SmartMeterRepository extends JpaRepository<SmartMeter, Long> {

    /*
     Methode verwendet Parameterbindung, d.h. der übergebene SmartMeterUser wird als Parameter in die Methode übergeben.
     Dadurch wird verhindert, dass unsichere Eingaben direkt in die Abfrage eingebunden und ausgeführt werden (SQL-Injection).
     Neben dem Sicherheitsaspekt wird auch die Lesbarkeit des Codes erhöht, da die Parameterbindung die Abfrage klarer strukturiert.
     Ein dritter Vorteil ist die verbesserte Performance, da die Datenbank die Abfrage wiederverwenden kann.
     */
    List<SmartMeter> findByUser(SmartMeterUser user);
}
